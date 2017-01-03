package com.kuduscreencast.timeseries

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, TimeZone}
import java.util.concurrent.TimeUnit

import com.cloudera.org.joda.time.DateTime
import com.google.common.collect.ImmutableList
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.kudu.{Schema, Type}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.MutableList

/**
  * Created by bosshart on 12/4/16.
  */
object CreateFixTable {

  val fixSchema: Schema = {
    val columns = ImmutableList.of(
      new ColumnSchemaBuilder("clordid", Type.STRING).key(true).build(),
      new ColumnSchemaBuilder("transacttime", Type.INT64).key(true).build(),
      new ColumnSchemaBuilder("msgtype", Type.STRING).key(false).build(),
      new ColumnSchemaBuilder("stocksymbol", Type.STRING).key(false).build(),
      new ColumnSchemaBuilder("orderqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("leavesqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("cumqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("avgpx", Type.DOUBLE).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("startdate", Type.INT64).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("enddate", Type.INT64).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("lastupdated", Type.INT64).key(false).build())
    new Schema(columns)
  }

  def main(args:Array[String]): Unit = {
    if (args.length < 3) {
      println("{kuduMaster} {tableName} {number of days/partitions}")
      return
    }
    val Array(kuduMaster, tableName, numberOfDaysStr) = args
    val numberOfDays = numberOfDaysStr.toInt
    var kuduOptions = Map(
      "kudu.table" -> tableName,
      "kudu.master" -> kuduMaster)

    val kuduContext = new KuduContext(kuduMaster)
    if(kuduContext.tableExists(tableName )) {
      System.out.println("Deleting existing table with same name.")
      kuduContext.deleteTable(tableName)
    }

    System.out.println("Creating new Kudu table " + tableName + " with " + numberOfDays + " date partitions. ")
    val options = new CreateTableOptions()
      .setRangePartitionColumns(ImmutableList.of("transacttime"))
      .addHashPartitions(ImmutableList.of("clordid"), 3)
      .setNumReplicas(1)

    val today = new DateTime().withTimeAtStartOfDay()
    val dayInMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
    for (i <- 0 until numberOfDays ){
      val lowerBound = fixSchema.newPartialRow()
      val lbMillis = today.plusDays(i).getMillis
      lowerBound.addLong("transacttime", lbMillis)
      val upperBound = fixSchema.newPartialRow()
      upperBound.addLong("transacttime", (lbMillis+dayInMillis-1))
      options.addRangePartition(lowerBound, upperBound)
    }
    kuduContext.createTable(tableName, KuduFixDataStreamer.schema, Seq("clordid","transacttime"),options)

    //kuduContext.syncClient.createTable(tableName, fixSchema, options)
    System.out.println("Successfully created " + tableName)

  }
}

