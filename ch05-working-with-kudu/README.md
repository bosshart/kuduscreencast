### Realtime Timeseries Example using "KIKS Stack" (Apache Kafka, Impala, Kudu, Spark Streaming)

This example shows how to build and run a project that demonstrates realtime data ingest, processing, and query. The project uses a FIX message generator to simulate the continious creation of Fix financial messages which are published to Kafka and then persisted to Kudu using a Spark Streaming job. Once the FIX data is persisted into Kudu, a small web application uses Impala (via JDBC) to feed a realtime chart that analyzes peak order activity in near-realtime. 

This example was testing using Kudu version 1.1, Kafka version 0.9.0 (included with Cloudera Distribution of Kafka version 2.0), Spark 1.6 and Impala 2.5 (included with CDH 5.8), and the [Impala JDBC Driver v2.5.36](http://www.cloudera.com/downloads/connectors/impala/jdbc/2-5-36.html). 

#### 1. Preparing the Kudu quickstart VM
When running this project, you'll need an environment running Kafka, Kudu, Impala, and Spark. These instructions assume you're using the [Kudu Quickstart VM](https://kudu.apache.org/docs/quickstart.html), but can easily be adapted to run on a "real", fully-distributed cluster. Since the Kudu quickstart doesn't come with Kafka, we'll need to start by installing Spark, Kafka, and Zookeeper. If you're running on a fully distributed Cloudera cluster, I'd recommend bypassing this step and going straight to Step 2, you can easily use Cloudera Manager to accomplish the same thing. 

Since the quickstart only has the CDH and Kudu repositories, add the Cloudera Kafka 2.0 repository. 

    cd /etc/yum.repos.d/
    sudo vi http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/cloudera-kafka.repo

Add the repository info for cloudera-kafka.repo

    [cloudera-kafka]
    # Packages for Cloudera's Distribution for kafka, Version 2, on RedHat	or CentOS 6 x86_64
    name=Cloudera's Distribution for kafka, Version 2
    baseurl=http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/2.0.1/
    gpgkey = http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/RPM-GPG-KEY-cloudera    
    gpgcheck = 1

Add or modify the cloudera-cdh5.repo 

    [cloudera-cdh5]
    # Packages for Cloudera's Distribution for Hadoop, Version 5, on RedHat	or CentOS 6 x86_64
    name=Cloudera's Distribution for Hadoop, Version 5
    baseurl=http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/5.8/
    gpgkey = http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera
    gpgcheck = 1

Install and start Zookeeper and Kafka. The zookeeper base package provides the basic libraries and scripts that are necessary to run ZooKeeper servers and clients. The zookeeper-server package contains the init.d scripts necessary to run ZooKeeper as a daemon process. Because zookeeper-server depends on zookeeper, installing the server package automatically installs the base package.

    sudo yum install zookeeper-server
    mkdir -p /var/lib/zookeeper
    sudo chown -R zookeeper /var/lib/zookeeper/
    sudo service zookeeper-server init
    sudo service zookeeper-server start
    
    sudo yum install kafka
    sudo yum install kafka-server
    sudo service kafka-server start
    
Install Spark

    sudo yum install spark-core spark-master spark-worker

Configure Spark. Edit /etc/spark/conf/spark-env.sh and change hostname in the last line to the name of the host where the Spark Master will run:

    export STANDALONE_SPARK_MASTER_HOST=quickstart.cloudera

Start Spark  

    sudo service spark-master start
    sudo service spark-worker start
    

#### 2. Install JDBC drivers and build the code. 

Run the following on the host where you want to build the application. If running from the kudu quickstart you'll need to install maven. 

The webapp that feeds the FIX message visualization connects to Kudu through Impala, meaning you will need to download the Impala JDBC driver jars and make them available to maven as a dependency. For more information on how to build and run a Maven-based project to execute SQL queries on Impala using JDBC, see [here](https://github.com/onefoursix/Cloudera-Impala-JDBC-Example). 
 
The pom dependency file for our project includes references to the following jars that are not available in public repos. 

    (1)  ImpalaJDBC41.jar
    (2)  TCLIServiceClient.jar
    (3)  hive_metastore.jar
    (4)  hive_service.jar
    (5)  ql.jar
    
Download the appropriate JDBC connector jars from the [cloudera website](http://www.cloudera.com/downloads/connectors/impala/jdbc/2-5-36.html), scp it (if needed), and unzip the file. 

    scp impala_jdbc_2.5.36.2056.zip demo@quickstart:/tmp
    unzip impala_jdbc_2.5.36.2056.zip
    cd 2.5.36.1056\ GA/
    unzip Cloudera_ImpalaJDBC41_2.5.36.zip

Install the JDBC drivers in your local maven repo: 

    mvn install:install-file -Dfile=ImpalaJDBC4.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ImpalaJDBC41 -Dversion=2.5.36 -Dpackaging=jar
    mvn install:install-file -Dfile=hive_service.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=hive_service -Dversion=2.5.36 -Dpackaging=jar
    mvn install:install-file -Dfile=hive_metastore.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=hive_metastore -Dversion=2.5.36 -Dpackaging=jar
    mvn install:install-file -Dfile=ql.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ql -Dversion=2.5.36 -Dpackaging=jar
    mvn install:install-file -Dfile=TCLIServiceClient.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=TCLIServiceClient -Dversion=2.5.36 -Dpackaging=jar

Clone the repo and build the project. 

    git clone https://github.com/bosshart/kuduscreencast.git
    cd kuduscreencast/ch05-working-with-kudu/
    mvn clean
    mvn package

Move the "sample app" to the quickstart VM or edge node as needed. 

    scp target/sample.app-1.0-SNAPSHOT.jar demo@quickstart.cloudera:/tmp/

#### 3. Create FIX Message Kafka Topic, Kudu, and Impala Table

The FIX message generator will publish order and execution messages to a Kafka topic. I named my kafka topic "fixdata" and configured it with one partition and no replicas so it could run on the quickstart VM.

    kafka-topics --zookeeper quickstart:2181 --create --topic fixdata --partitions 1 --replication-factor 1
    
If you want, you can test Kafka. In separate ssh sessions, run both: 

    kafka-console-producer --broker-list quickstart:9092 --topic fixdata
    kafka-console-consumer --zookeeper quickstart:2181 --topic fixdata    

You should be able input test in the "producer" side and see it emitted in the consumer. 

Next, create the corresponding Kudu and Impala tables. I also named the table "fixdata".
    
    java -cp com.kuduscreencast.timeseries.CreateFixTable quickstart fixdata 3
    impala-shell
    [quickstart.cloudera:21000] > CREATE EXTERNAL TABLE `fixdata` (
    `transacttime` BIGINT,
    `clordid` STRING,
    `msgtype` STRING,
    `stocksymbol` STRING,
    `orderqty` INT,
    `leavesqty` INT,
    `cumqty` INT,
    `avgpx` DOUBLE,
    `startdate` BIGINT,
    `enddate` BIGINT,
    `lastupdated` BIGINT
    )
    TBLPROPERTIES(
      'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
      'kudu.table_name' = 'fixdata',
      'kudu.master_addresses' = 'quickstart.cloudera:7051',
      'kudu.key_columns' = 'transacttime, clordid'
    );
    

#### 4. Run the Application

    
Run the Fix message generator

    spark-submit --master spark://:quickstart.cloudera  --class com.kuduscreencast.timeseries.FIXGenerator sample.app-1.0-SNAPSHOT.jar quickstart:9092 fixdata

Run the Spark Streaming application to populate Kudu: 
    
    spark-submit --master spark://:quickstart.cloudera  --class com.kuduscreencast.timeseries.KuduFixDataStreamer sample.app-1.0-SNAPSHOT.jar quickstart:9092 fixdata quickstart fixdata

Finally, run the web application using jetty: 

    mvn jetty:run
    

If you go to web-app-hostname:8080, you should see a visualization showing the largest order within that ten second window for each stock symbol. The chart should automatically refresh it's data every 10 seconds.  
