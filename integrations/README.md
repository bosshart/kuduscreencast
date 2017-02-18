### Integrating Kudu with Flume and Spark

Before running these examples, you'll need flume, git, and maven installed. You'll also need some sample data downloaded and staged in Kudu. There's a script in the project to automate this:  

    cd integrations/
    ./bootstrap.sh
 
About these examples: 

* Segment 1 demonstrates integrating Kudu with Apache Flume. The steps to get flume running are in `flume_example.sh`. 
* Segment 2 is the first of two segments looking at the integration of Kudu with Apache Spark. See code in `CreateTable.scala` and `SparkExample.scala`. 
* Segment 3 is the second Kudu-Spark segment, create a new Kudu table using Spark and then some fun with Spark MLLib. See `SparkExample.scala`. 

    