package com.kuduscreencast.example

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.client.shaded.com.google.common.collect.ImmutableList
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}


object CreateTable {

  val newSchema =
    StructType(
      StructField("movieid", IntegerType, false) ::
      StructField("userid", IntegerType, false) ::
      StructField("rating", IntegerType, true) ::
      StructField("age", IntegerType, true) ::
      StructField("gender", StringType, true) ::
      StructField("occupation", StringType, true) ::
      StructField("zip", IntegerType, true) ::
      StructField("movietitle", StringType, true) ::
      StructField("releasedate", StringType, true) ::
      StructField("videoreleasedate", StringType, true) ::
      StructField("url", StringType, true) ::
      StructField("ts", IntegerType, true) :: Nil)

  def main(args:Array[String]): Unit = {
    if (args.length == 0) {
      println("{kuduMaster} {tableName}")
      return
    }
    val kuduMaster = args(0)
    val tableName = args(1)

    var kuduOptions = Map(
      "kudu.table" -> args(1),
      "kudu.master" -> args(0))

    val kuduContext = new KuduContext(kuduMaster)

    if(kuduContext.tableExists(tableName )) {
      kuduContext.deleteTable(tableName)
    }

    kuduContext.createTable(tableName, newSchema, Seq("movieid","userid"),
      new CreateTableOptions().addHashPartitions(ImmutableList.of("movieid"), 3)
        .setNumReplicas(1))

  }
}

