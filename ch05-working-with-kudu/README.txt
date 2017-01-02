

#Install Kafka on quickstart VM


cd /etc/yum.repos.d/
sudo wget http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/cloudera-kafka.repo


# The zookeeper base package provides the basic libraries and scripts that are necessary to run ZooKeeper servers and clients. The documentation is also included in this package.
# The zookeeper-server package contains the init.d scripts necessary to run ZooKeeper as a daemon process. Because zookeeper-server depends on zookeeper, installing the server package automatically installs the base package.

sudo yum install zookeeper
sudo yum install zookeeper-server
mkdir -p /var/lib/zookeeper
sudo chown -R zookeeper /var/lib/zookeeper/
sudo service zookeeper-server init
sudo service zookeeper-server start

sudo yum install kafka
sudo yum install kafka-server
sudo service kafka-server start


//check connections:
zookeeper-client
ls /brokers/ids



# KuduExamples


Run the fix data producer:


Run the Spark Streaming job:

StockStreamer quickstart:9092 fixdata quickstart fixdata

  <brokers> is a list of one or more Kafka brokers
  <topics> is a list of one or more kafka topics to consume from
  <kuduMasters> is a list of one or more Kudu masters
  <tableName> is the name of the kudu table
  <local> 'local' to run in local mode



Error:(107, 49) overloaded method value createDirectStream with alternatives:
org.apache.spark.streaming.api.java.JavaStreamingContext,keyClass: Class[String],valueClass: Class[String],keyDecoderClass: Class[kafka.serializer.StringDecoder],valueDecoderClass: Class[kafka.serializer.StringDecoder],kafkaParams: java.util.Map[String,String],topics: java.util.Set[String])org.apache.spark.streaming.api.java.JavaPairInputDStream[String,String] <and>
org.apache.spark.streaming.StreamingContext,kafkaParams: scala.collection.immutable.Map[String,String],topics: scala.collection.immutable.Set[String])(implicit evidence$19: scala.reflect.ClassTag[String], implicit evidence$20: scala.reflect.ClassTag[String], implicit evidence$21: scala.reflect.ClassTag[kafka.serializer.StringDecoder], implicit evidence$22: scala.reflect.ClassTag[kafka.serializer.StringDecoder])org.apache.spark.streaming.dstream.InputDStream[(String, String)]
org.apache.spark.streaming.StreamingContext, scala.collection.mutable.Map[String,String], scala.collection.immutable.Set[String])
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
                                                ^

Start the SS job
rbtest-1.vpc.cloudera.com:9092,rbtest-2.vpc.cloudera.com:9092,rbtest-3.vpc.cloudera.com:9092 marketdata rbtest-1.vpc.cloudera.com tickdb local


---------------
go to http://www.cloudera.com/downloads/connectors/impala/jdbc/2-5-36.html

Download the driver and install it in your local maven repository:

        <dependency>
            <groupId>com.cloudera.impala.jdbc</groupId>
            <artifactId>hive_metastore</artifactId>
            <version>${impala.jdbc.version}</version>
        </dependency>
        <dependency>
            <groupId>com.cloudera.impala.jdbc</groupId>
            <artifactId>hive_service</artifactId>
            <version>${impala.jdbc.version}</version>
        </dependency>
        <dependency>
            <groupId>com.cloudera.impala.jdbc</groupId>
            <artifactId>ql</artifactId>
            <version>${impala.jdbc.version}</version>
        </dependency>
        <dependency>
            <groupId>com.cloudera.impala.jdbc</groupId>
            <artifactId>TCLIServiceClient</artifactId>
            <version>${impala.jdbc.version}</version>
        </dependency>

mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GAImpalaJDBC41.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ImpalaJDBC41 -Dversion=2.5.36 -Dpackaging=jar


mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/ImpalaJDBC4.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ImpalaJDBC42 -Dversion=2.5.36 -Dpackaging=jar
mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/hive_service.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=hive_service -Dversion=2.5.36 -Dpackaging=jar
mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/hive_metastore.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=hive_metastore -Dversion=2.5.36 -Dpackaging=jar
mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/ql.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ql -Dversion=2.5.36 -Dpackaging=jar
mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/TCLIServiceClient.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=TCLIServiceClient -Dversion=2.5.36 -Dpackaging=jar


Sample output
^Cbosshart-MBP:Cloudera_ImpalaJDBC4_2.5.36 bosshart$ mvn install:install-file -Dfile=/Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/ImpalaJDBC4.jar -DgroupId=com.cloudera.impala.jdbc -DartifactId=ImpalaJDBC41 -Dversion=2.5.36 -Dpackaging=jar
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Maven Stub Project (No POM) 1
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-install-plugin:2.4:install-file (default-cli) @ standalone-pom ---
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-settings/2.0.6/maven-settings-2.0.6.jar
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-plugin-registry/2.0.6/maven-plugin-registry-2.0.6.jar
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-profile/2.0.6/maven-profile-2.0.6.jar
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-project/2.0.6/maven-project-2.0.6.jar
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-model/2.0.6/maven-model-2.0.6.jar
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-plugin-registry/2.0.6/maven-plugin-registry-2.0.6.jar (29 KB at 32.9 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-artifact-manager/2.0.6/maven-artifact-manager-2.0.6.jar
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-profile/2.0.6/maven-profile-2.0.6.jar (35 KB at 39.9 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-repository-metadata/2.0.6/maven-repository-metadata-2.0.6.jar
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-settings/2.0.6/maven-settings-2.0.6.jar (48 KB at 53.0 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-artifact/2.0.6/maven-artifact-2.0.6.jar
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-repository-metadata/2.0.6/maven-repository-metadata-2.0.6.jar (24 KB at 26.4 KB/sec)
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-project/2.0.6/maven-project-2.0.6.jar (114 KB at 123.1 KB/sec)
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-model/2.0.6/maven-model-2.0.6.jar (85 KB at 92.0 KB/sec)
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-artifact-manager/2.0.6/maven-artifact-manager-2.0.6.jar (56 KB at 60.2 KB/sec)
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-artifact/2.0.6/maven-artifact-2.0.6.jar (86 KB at 89.4 KB/sec)
[INFO] Installing /Users/bosshart/Downloads/2.5.36GA/Cloudera_ImpalaJDBC4_2.5.36/ImpalaJDBC4.jar to /Users/bosshart/.m2/repository/com/cloudera/impala/jdbc/ImpalaJDBC41/2.5.30/ImpalaJDBC41-2.5.30.jar
[INFO] Installing /var/folders/m3/1fjrfwcn071877fklbjy0hb00000gp/T/mvninstall414333275538749166.pom to /Users/bosshart/.m2/repository/com/cloudera/impala/jdbc/ImpalaJDBC41/2.5.30/ImpalaJDBC41-2.5.30.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.778 s
[INFO] Finished at: 2016-12-28T19:49:13-06:00
[INFO] Final Memory: 11M/245M
[INFO] ------------------------------------------------------------------------
bosshart-MBP:Cloudera_ImpalaJDBC4_2.5.36 bosshart$


<dependency>
  <groupId>com.cloudera.impala.jdbc</groupId>
  <artifactId>ImpalaJDBC42</artifactId>
  <version>2.5.36</version>
</dependency>


com.cloudera.impala.jdbc42.Driver

CREATE EXTERNAL TABLE `fixdata` (
`clordid` STRING,
`transacttime` BIGINT,
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
  'kudu.key_columns' = 'clordid, transacttime'
);

----------------




Notes:

CREATE TABLE `tickdb` (
`ticket` STRING,
`ts` BIGINT,
`per` STRING,
`last` STRING,
`vol` STRING
)
DISTRIBUTE BY HASH (ticket) INTO 4 BUCKETS
TBLPROPERTIES(
  'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
  'kudu.table_name' = 'tickdb',
  'kudu.master_addresses' = 'rbtest-1.vpc.cloudera.com:7051',
  'kudu.key_columns' = 'ticket, ts'
);

CREATE VIEW real_tickdb AS SELECT ticket, cast(ts/1000 as timestamp) as ts,per,vol FROM tickdb;

create view regression_tests.TABLE1 as select
cast(ID as int),
cast(salesdate/1000 as timestamp) as salesdate,
cast(amount as double),
cast(salesamount as decimal(9,4)),
state


select `symbol`, leavesqty,cumqty, avgpx from fixdata where msgtype="8" order by transacttime limit 10;

SELECT avgpx, from_unixtime(cast(transacttime/1000 as int),"yyyy-MM-dd HH:mm:ss") as ts FROM fixdata WHERE msgtype="8" AND cast(transacttime/1000 as timestamp) > now() - interval 1 day LIMIT 10;


select stocksymbol, cast(transacttime/1000 as timestamp) as ts, orderqty,
    cast(
      orderqty - lag(orderqty,1) over
        (partition by stocksymbol order by transacttime)
      as decimal(8,2)
    )
    as "change from yesterday"
  from fixdata
    where msgtype="D" AND cast(transacttime/1000 as timestamp) > now() - interval 1 minute AND stocksymbol="AAPL"
    order by transacttime desc;


SELECT stocksymbol, trunc(cast(transacttime/1000 as timestamp), "MI") AS time_window, max(orderqty) AS max_order
    FROM fixdata
    WHERE cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 10 minutes) AND stocksymbol="AAPL"
    GROUP BY stocksymbol, time_window
    ORDER BY time_window;

select clordid, cast((cast(transacttime/10000 as int)*10000)/1000 as timestamp) FROM fixdata
    WHERE cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 1 minutes)

select clordid, cast(transacttime/10000 as int) FROM fixdata
    WHERE cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 1 minutes)

SELECT stocksymbol, cast((cast(transacttime/10000 as int)*10000)/1000 as timestamp) AS 10_s_time_window, max(orderqty) AS max_order FROM fixdata
    WHERE cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 10 minutes)
    AND cast(transacttime/1000 as timestamp) < date_sub(to_utc_timestamp(now(),'PDT'), interval 10 seconds)
    GROUP BY stocksymbol, 10_s_time_window
    ORDER BY stocksymbol, 10_s_time_window;


trunc(cast(transacttime/1000 as timestamp), "MI")

SELECT truncate(transacttime,3), sum(orderqty)
    OVER (PARTITION BY truncate(transacttime,3)) AS total
    FROM fixdata
    WHERE msgtype="D" AND cast(transacttime/1000 as timestamp) > to_utc_timestamp(now()) - interval 10 second AND stocksymbol="AAPL"
    ORDER by transacttime desc;

1483047845621000         | 1048
1483047845622000
SELECT clordid, truncate(transacttime,3), msgtype, leavesqty,
  sum(orderqty) OVER (PARTITION BY clordid) AS number_of_orders,
  count() OVER (PARTITION BY clordid) AS number_of_orders
  from fixdata
    where cast(transacttime/1000 as timestamp) > now() - interval 10 second AND stocksymbol="AAPL"
    order by clordid, transacttime desc;



SELECT stocksymbol, orderqty, cast(transacttime/1000 as timestamp) as ts FROM fixdata
    WHERE msgtype="D" AND cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 10 minutes)
    ORDER BY stocksymbol, ts;


Kafka-to-kafka tests:

kafka-topics --zookeeper ip-10-1-1-228.us-west-2.compute.internal:2181 --delete --topic source_topic
kafka-topics --zookeeper ip-10-1-1-228.us-west-2.compute.internal:2181 --delete --topic dest_topic


kafka-topics --zookeeper ip-10-1-1-228.us-west-2.compute.internal:2181 --create --topic source_topic --partitions 2 --replication-factor 1
kafka-topics --zookeeper ip-10-1-1-228.us-west-2.compute.internal:2181 --create --topic dest_topic --partitions 2 --replication-factor 1




kafka-console-consumer --zookeeper ip-10-1-1-228.us-west-2.compute.internal:2181 --topic source_topicTestKuduClient


package com.kuduscreencast.timeseries

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.util.regex.{Matcher, Pattern}

import com.google.common.collect.ImmutableList
import kafka.serializer.StringDecoder
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Type, Schema}
import org.apache.kudu.client.{AlterTableOptions, CreateTableOptions, Operation, KuduClient}
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{SQLContext, Row, DataFrame}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.types._

import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.Map



object StockStreamer {

  val log = Logger.getRootLogger.setLevel(Level.ERROR)

  val newSchema =
    StructType(
      StructField("clordid", StringType, false) ::
        StructField("transacttime", LongType, false) ::
        StructField("msgtype", StringType, true) ::
        StructField("symbol", StringType, true) ::
        StructField("orderqty", IntegerType, true) ::
        StructField("leavesqty", IntegerType, true) ::
        StructField("cumqty", IntegerType, true) ::
        StructField("avgpx", DoubleType, true) ::
        StructField("startdate", LongType, true) ::
        StructField("enddate", LongType, true) ::
        StructField("lastupdated", StringType, true) :: Nil)

  val fixSchema: Schema = {
    val columns = ImmutableList.of(
      new ColumnSchemaBuilder("clordid", Type.STRING).key(true).build(),
      new ColumnSchemaBuilder("transacttime", Type.INT64).key(true).build(),
      new ColumnSchemaBuilder("msgtype", Type.STRING).key(false).build(),
      new ColumnSchemaBuilder("symbol", Type.STRING).key(false).build(),
      new ColumnSchemaBuilder("orderqty", Type.INT32).key(false).build(),
      new ColumnSchemaBuilder("leavesqty", Type.INT32).key(false).build(),
      new ColumnSchemaBuilder("cumqty", Type.INT32).key(false).build(),
      new ColumnSchemaBuilder("avgpx", Type.DOUBLE).key(false).build(),
      new ColumnSchemaBuilder("startdate", Type.INT64).key(false).build(),
      new ColumnSchemaBuilder("enddate", Type.INT64).key(false).build(),
      new ColumnSchemaBuilder("lastupdated", Type.STRING).key(false).build())
    new Schema(columns)
  }


  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      System.err.println(
        """
          |Usage: StockStreamer <brokers> <topics> <kuduMaster> <tableName>
          |  <brokers> is a list of one or more Kafka brokers
          |  <topics> is a list of one or more kafka topics to consume from
          |  <kuduMasters> is a list of one or more Kudu masters
          |  <tableName> is the name of the kudu table
          |  <local> 'local' to run in local mode
          |
        """.stripMargin)
      System.exit(1)
    }

    val Array(brokers, topics, kuduMaster, tableName, local) = args
    val runLocal = local.equals("local")
    val sparkConf = new SparkConf().setAppName("Kudu StockStreamer")
    var ssc:StreamingContext = null
    var sqlContext:SQLContext = null
    val createNewTable: Boolean = false

    if (runLocal) {
      println("Running Local")
      val sparkConfig = new SparkConf()
      //sparkConfig.set("spark.broadcast.compress", "false")
      //sparkConfig.set("spark.shuffle.compress", "false")
      //sparkConfig.set("spark.shuffle.spill.compress", "false")
      //sparkConfig.set("spark.io.compression.codec", "lzf")
      val sc = new SparkContext("local[4]", "SparkSQL on Kudu", sparkConfig)
      sqlContext = new SQLContext(sc)
      ssc = new StreamingContext(sc, Seconds(2))
    } else {
      println("Running Cluster")
      ssc = new StreamingContext(sparkConf, Seconds(2))
    }

    var kuduContext: KuduContext = new KuduContext(kuduMaster)
    createTable(kuduContext, tableName)



    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "spark.streaming.kafka.maxRetries" -> "5")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    val fixMessages = messages.map(_._2)
    fixMessages.print(10)

    /**

    val parsed = messages.map(line => {
      parseFixEvent(line._2)
    })
    val parsedDf = parsed.foreachRDD(rdd => {
      val jRdd = rdd.toJavaRDD()
      val df = sqlContext.createDataFrame(jRdd, newSchema )
      kuduContext.insertRows(df, tableName)
    }) **/

    /**val parseFixLines = messages.transform( rdd =>
      *val parsed = rdd.map(tickLine => (TickEventBuilder.build(tickLine._2)))
      *parsed.foreachPartition(it => {

      *val lines = messages.map(line => {

      *})

      *lines.transform(rdd => {

      *})
      *lines.transform( rdd => {
      *sqlContext.createDataFrame(rdd, fixSchema)
      *})**/


    //val eventsDf = spark.createDataFrame(rows, schema)
    //lines.toDF



    //val fixMessages = messages.map(_._2)
    //fixMessages.print(10)


    /** parsed.foreachPartition(it => {
      * val pClient = kuduContext.syncClient //new KuduClientBuilder(kuduMaster).build()
      * val table = pClient.openTable(tableName)
      * val kuduSession = pClient.newSession()
      * kuduSession.setFlushMode(FlushMode.AUTO_FLUSH_BACKGROUND)
      * kuduSession.setIgnoreAllDuplicateRows(true)
      * var insert: Operation = null
      * try {
      * it.foreach(event => {
      * insert = table.newUpsert()
      * val row = insert.getRow
      * row.addString("ticksymbol",event.tickId)
      * val dateTime = convertTimestamp(event.day, event.time)
      * val ts = getTimstamp(dateTime).toLong
      * row.addLong("time", ts)

      * //row.addLong("ts", ts)

      * row.addString("per", event.period)

      * //row.addDouble("price", Double.parseDouble(event.last))
      * row.addInt("vol", Integer.parseInt(event.volume))

      * val r = kuduSession.apply(insert)

      * })
      * } catch {
      * case e:Exception => e.printStackTrace()

      * } finally {
      * kuduSession.close()
      * }
      * // no pClient shutdown needed, handled by kudu context
      * }) **/


    //parsedTickLines.count().print()

    sys.ShutdownHookThread {
      println("Gracefully stopping Spark Streaming Application")
      ssc.stop(true, true)
      println("Application stopped")
    }

    // Start the computation
    //ssc.checkpoint("./checkpoint")
    ssc.start()
    ssc.awaitTermination()
  }



  /**def parseFixEvent(eventString: String) : Row = { //DataFrame
    var fixElements = scala.collection.mutable.Map[String,String]()
    val pattern = """(\d{2})=(.*?)(?:[0-9]{2}=)""".r
    pattern.findAllIn(eventString).matchData foreach {
      m => fixElements. += (m.group(1) -> m.group(2))
    }

    Row(fixElements("11") /* 11: clordid */,
      fixElements("60").toLong /* 60: transacttime */,
      fixElements("35") /* 35: msgtype */,
      fixElements("55") /* 55: symbol */,
      fixElements("38").toInt /* 38: orderqty */,
      fixElements("151").toInt /* 151: leavesqty */,
      fixElements("14").toInt /* 14: cumqty */,
      fixElements("6").toDouble /* 6: avgpx */,
      if (fixElements("35").equals("D")) fixElements("60").toLong else null /* 60: transacttime */,
      if (fixElements("151")==0) fixElements("60").toLong else null /* 60: transacttime */,
      System.currentTimeMillis())
  }**/


  def convertTimestamp(dayStr: String, timeStr: String): String = {
    var dateStr = dayStr + timeStr
    var out = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    out.format((new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr)))
  }

  def getTimstamp(tsStr: String): Long = {
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    val parsedTime = format.parse(tsStr)
    var ts = new java.sql.Timestamp(parsedTime.getTime());
    ts.getTime
  }

  def createTable(context: KuduContext, tableName: String): Unit = {
    if(context.tableExists(tableName )) {
      context.deleteTable(tableName)
    }

    val options = new CreateTableOptions()
      .addHashPartitions(ImmutableList.of("clordid"), 3)
      .setRangePartitionColumns(ImmutableList.of("startdate"))
      .setNumReplicas(1)

    val daySplits: List[String] = List("2016-05-05 00:00:00","2016-05-06 00:00:00", "2016-05-07 00:00:00")
    daySplits.map(splitStr=>getTimstamp(splitStr)).foreach { splitValue =>
      val lowerBound = fixSchema.newPartialRow()
      val dayInMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
      lowerBound.addLong("startdate", splitValue-dayInMillis)
      val upperBound = fixSchema.newPartialRow();
      upperBound.addLong("startdate", (splitValue-1))
      options.addRangePartition(lowerBound, upperBound)
    }
    context.syncClient.createTable(tableName, fixSchema, options)
  }
}

35=837=69d97bd9-92f9-4185-ba3d-9dc42663a60011=6d129ad5-468e-4043-adaa-2613ec5af16e17=a50b37d7-c15d-41e9-ad5e-c81e7c77421c20=0150=039=255=AAPL54=1151=014=61596=0.965018960=148296614202010=000

35=837=32826932-83f9-41cc-9ae2-0d055bd3c2e711=aed21bd4-c0e9-42b9-8056-fc53547f5f4817=d666bc0b-3f38-4a63-8d07-26a429ed008120=0150=039=155=AAPL54=1151=18114=66466=0.4823818860=148295604541110=000

(\d{2})=(.*?)(?:[0-9]{2}=)
(\d{2})=(.*?)(?:[0-9]{2}=)

35=837=32826932-83f9-41cc-9ae2-0d055bd3c2e711=aed21bd4-c0e9-42b9-8056-fc53547f5f48
17=d666bc0b-3f38-4a63-8d07-26a429ed0081
20=01
50=0
39=1
55=AAPL
54=11
51=181
14=664
66=0.48238188
60=1482956045411
10=000