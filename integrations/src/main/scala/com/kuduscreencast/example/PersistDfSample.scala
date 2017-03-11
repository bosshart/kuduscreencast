package com.kuduscreencast.example


import com.google.common.collect.ImmutableList
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext

object PersistDfSample {
  def main(args:Array[String]): Unit = {

    val kuduMaster = "quickstart.cloudera"
    val sparkConf = new SparkConf().setAppName("Spark-Kudu Sample")
    sparkConf.setMaster("local").set("spark.ui.enabled", "false")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    sqlContext.setConf("spark.sql.parquet.binaryAsString","true")

    val movieDf = sqlContext.read.load("hdfs://quickstart.cloudera:8022/user/hive/warehouse/movie_data/")

    val kuduContext = new KuduContext("quickstart.cloudera")
    val newTable = "new_kudu_table"

    kuduContext.createTable(newTable,
      movieDf.schema,
      Seq("movieid","userid"),
      new CreateTableOptions().addHashPartitions(ImmutableList.of("movieid"),3).setNumReplicas(1)
    )
    kuduContext.insertRows(movieDf, newTable)

    val results = sqlContext.read.options(Map("kudu.table" -> "new_kudu_table", "kudu.master" -> "quickstart.cloudera")).format("org.apache.kudu.spark.kudu").load
    results.show(10)



  }

}
