package com.kuduscreencast.example

import java.util

import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.client.KuduClient.KuduClientBuilder
import org.apache.kudu.client.shaded.com.google.common.collect.ImmutableList
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.kudu.{Type, Schema, ColumnSchema}
import org.apache.kudu.client.{CreateTableOptions, KuduClient}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{StringType, IntegerType, StructField, StructType}

/**
  * Created by bosshart on 12/4/16.
  */
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

  private def getSqlContext(appName: String, isLocal: Boolean): SQLContext = {
    val sparkConf = new SparkConf().setAppName(appName)
    if (isLocal) {
      sparkConf.setMaster("local").
        set("spark.ui.enabled", "false")
    }
    val sc = new SparkContext(sparkConf)
    new SQLContext(sc)
  }
}

