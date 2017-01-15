package com.kuduscreencast.timeseries

import java.util.concurrent.TimeUnit

import com.cloudera.org.joda.time.DateTime
import com.google.common.collect.ImmutableList
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Type, Schema}
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.types._


object CreateFixTable {

  val fixSchema: Schema = {
    val columns = ImmutableList.of(
      new ColumnSchemaBuilder("transacttime", Type.INT64).key(true).build(),
      new ColumnSchemaBuilder("stocksymbol", Type.STRING).key(true).build(),
      new ColumnSchemaBuilder("clordid", Type.STRING).key(true).build(),
      new ColumnSchemaBuilder("msgtype", Type.STRING).key(false).build(),
      new ColumnSchemaBuilder("orderqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("leavesqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("cumqty", Type.INT32).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("avgpx", Type.DOUBLE).nullable(true).key(false).build(),
      new ColumnSchemaBuilder("lastupdated", Type.INT64).key(false).build())
    new Schema(columns)
  }

  val schema =
    StructType(
      StructField("transacttime", LongType, false) ::
        StructField("stocksymbol", StringType, false) ::
        StructField("clordid", StringType, false) ::
        StructField("msgtype", StringType, false) ::
        StructField("orderqty", IntegerType, true) ::
        StructField("leavesqty", IntegerType, true) ::
        StructField("cumqty", IntegerType, true) ::
        StructField("avgpx", DoubleType, true) ::
        StructField("lastupdated", LongType, false) :: Nil)

  def main(args:Array[String]): Unit = {
    if (args.length < 5) {
      println("{kuduMaster} " +
              "{tableName} " +
              "{number hash partitions} " +
              "{number of range partitions (by day)} " +
              "{'quickstart' to use single tablet replica, anything else uses 3 replicas}")
      return
    }
    val Array(kuduMaster, tableName, numberOfHashPartitionsStr, numberOfDaysStr, quickstart) = args
    val tabletReplicas = if(quickstart.equals("quickstart")) { 1 } else { 3 }
    val numberOfHashPartitions = numberOfDaysStr.toInt
    val numberOfDays = numberOfDaysStr.toInt

    val kuduContext = new KuduContext(kuduMaster)
    if(kuduContext.tableExists(tableName )) {
      System.out.println("Deleting existing table with same name.")
      kuduContext.deleteTable(tableName)
    }

    val options = new CreateTableOptions()
      .setRangePartitionColumns(ImmutableList.of("transacttime"))
      .addHashPartitions(ImmutableList.of("stocksymbol"),numberOfHashPartitions)
      .setNumReplicas(tabletReplicas)
    val today = new DateTime().withTimeAtStartOfDay() //today at midnight
    val dayInMillis = TimeUnit.MILLISECONDS.convert(1,TimeUnit.DAYS) //1 day in millis
    for (i <- 0 until numberOfDays) {
      val lbMillis = today.plusDays(i).getMillis
      val upMillis = lbMillis+dayInMillis-1
      val lowerBound = fixSchema.newPartialRow()
      lowerBound.addLong("transacttime",lbMillis)
      val upperBound = fixSchema.newPartialRow()
      upperBound.addLong("transacttime",upMillis)
      options.addRangePartition(lowerBound,upperBound)
    }
    kuduContext.createTable(tableName, schema, Seq("transacttime","stocksymbol","clordid"),options)

    System.out.println("Created new Kudu table " + tableName + " with " + numberOfHashPartitions + " hash partitions and " + numberOfDays + " date partitions. ")


  }
}

