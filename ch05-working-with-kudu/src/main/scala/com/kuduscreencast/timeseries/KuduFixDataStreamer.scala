package com.kuduscreencast.timeseries

import kafka.serializer.StringDecoder
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


object KuduFixDataStreamer {
  val schema =
    StructType(
      StructField("transacttime", LongType, false) ::
      StructField("clordid", StringType, false) ::
      StructField("msgtype", StringType, true) ::
      StructField("stocksymbol", StringType, true) ::
      StructField("orderqty", IntegerType, true) ::
      StructField("leavesqty", IntegerType, true) ::
      StructField("cumqty", IntegerType, true) ::
      StructField("avgpx", DoubleType, true) ::
      StructField("startdate", LongType, true) ::
      StructField("enddate", LongType, true) ::
      StructField("lastupdated", LongType, true) :: Nil)

  def main(args: Array[String]): Unit = {

    if (args.length < 5) {
      System.err.println(
        """
                            |Usage: StockStreamer <brokers> <topics> <kuduMaster> <tableName>
                            |  <brokers> is a list of one or more Kafka brokers
                            |  <topic> kafka topic to consume from
                            |  <kuduMasters> is a list of one or more Kudu masters
                            |  <tableName> is the name of the kudu table
                            |  <local> 'local' to run in local mode, else anything else for cluster
                            |
        """.stripMargin)
      System.exit(1)
    }

    val Array(brokers, topics, kuduMaster, tableName, local) = args
    val runLocal = local.equals("local")
    val sparkConf = new SparkConf().setAppName("Kudu StockStreamer")
    if (runLocal) {
      println("Running Local")
      sparkConf.setMaster("local")
    } else {
      println("Running Cluster")
    }
    val sc = new SparkContext(sparkConf);
    val sqlContext = new SQLContext(sc)
    val ssc = new StreamingContext(sc, Seconds(5))
    val createNewTable: Boolean = false


    val broadcastSchema = sc.broadcast(schema)
    var kuduContext: KuduContext = new KuduContext(kuduMaster)
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "spark.streaming.kafka.maxRetries" -> "5")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    val parsed = messages.map(line => {
      parseFixEvent(line._2)
    })
    val parsedDf = parsed.foreachRDD(rdd => {
      val df = sqlContext.createDataFrame(rdd, broadcastSchema.value)
      kuduContext.upsertRows(df, tableName)
    })

    sys.ShutdownHookThread {
      println("Gracefully stopping Spark Streaming Application")
      ssc.stop(true, true)
      println("Application stopped")
    }

    // Start the computation
    ssc.checkpoint("./checkpoint")
    ssc.start()
    ssc.awaitTermination()
  }


  def parseFixEvent(eventString: String) : Row = {
    var fixElements = scala.collection.mutable.Map[String,String]()
    val pattern = """(\d*)=(.*?)(?:[\001])""".r
    pattern.findAllIn(eventString).matchData foreach {
      m => fixElements. += (m.group(1) -> m.group(2))
    }

    try {
      Row(fixElements("60").toLong /* 60: transacttime */,
        fixElements("11") /* 11: clordid */,
        fixElements("35") /* 35: msgtype */,
        fixElements("55") /* 55: symbol */,
        if (fixElements("35").equals("D")) fixElements("38").toInt else null /* 38: orderqty */,
        if (fixElements.contains("151")) fixElements("151").toInt else null /* 151: leavesqty */,
        if (fixElements.contains("14")) fixElements("14").toInt else null /* 14: cumqty */,
        if (fixElements.contains("6")) fixElements("6").toDouble else null /* 6: avgpx */,
        if (fixElements("35").equals("D")) fixElements("60").toLong else null /* 60: transacttime */,
        if (fixElements.contains("151") && fixElements("151")==0) fixElements("60").toLong else null /* 60: transacttime */,
        System.currentTimeMillis())
    } catch {
        case e:Exception => e.printStackTrace()
        null
    }

  }
}
