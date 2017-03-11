package com.kuduscreencast.example

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.client.shaded.com.google.common.collect.ImmutableList
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}



/**
  * Created by bosshart on 12/4/16.
  *
  * Demonstrates a couple ways to read data from Kudu using Spark.
  * Usage: SparkExample <kudumaster> <kudutablename>
  *   <kudumaster> is/are the kudu master hosts
  *   <kudutablename> is the name of the "movie table" created separately
  */
object SparkExample {


  def main(args:Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    if (args.length == 0) {
      println("{kuduMaster} {tableName}")
      return
    }
    val kuduMaster = args(0)
    val tableName = args(1)

    val sparkConf = new SparkConf().setAppName("Spark-Kudu Example")
    sparkConf.setMaster("local").set("spark.ui.enabled", "false")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    var kuduOptions = Map(
      "kudu.table" -> args(1),
      "kudu.master" -> args(0))


    val df = sqlContext.read.options(kuduOptions).format("org.apache.kudu.spark.kudu").load
    val favoriteMovieDf = df.filter("movieid=250").select("movietitle")
    favoriteMovieDf.show()
    // same thing with SQL syntax
    df.registerTempTable("movies")
    val sampleDf = sqlContext.sql(s"""SELECT movietitle FROM movies WHERE movieid=250 LIMIT 10""")
    sampleDf.show()


    //next lets do an update
    val kuduContext = new KuduContext(kuduMaster)
    val fixRating: Integer=>Integer = (arg: Integer) => { if (arg <= 3) 5 else arg }
    val fixRatingUdf = udf(fixRating)
    val fixedRatings = sqlContext.sql(s"""SELECT * FROM movies WHERE movietitle="Top Gun (1986)" """).withColumn("rating", fixRatingUdf(col("rating")))
    kuduContext.updateRows(fixedRatings,tableName)

    // try this first, oh it fails, deletes should only specify the key columns
    //val topGunDf = sqlContext.sql(s"""SELECT movieid, userid, rating FROM movies WHERE movietitle="Dirty Dancing (1987)" and rating < 3 """)
    //kuduContext.deleteRows(topGunDf,tableName)

    // do this instead
    val topGunDf2 = sqlContext.sql(s"""SELECT movieid, userid FROM movies WHERE movietitle="Dirty Dancing (1987)" and rating < 3 """)
    kuduContext.deleteRows(topGunDf2,tableName)


    val allMovies = sqlContext.read.options(kuduOptions).format("org.apache.kudu.spark.kudu").load
    val Array(training, test) = allMovies.randomSplit(Array(0.8, 0.2))

    training.cache()
    val als = new ALS()
      .setMaxIter(5)
      .setUserCol("userid")
      .setItemCol("movieid")
      .setRatingCol("rating")
    val model = als.fit(training)

    val predictions = model.transform(test)
    predictions.printSchema()
    predictions.show(10)

    val modifiedTable = "movie_rating_predictions"
    if(kuduContext.tableExists(modifiedTable)) {
      kuduContext.deleteTable(modifiedTable)
    }

    kuduContext.createTable(modifiedTable, predictions.schema, Seq("movieid","userid"),
      new CreateTableOptions().addHashPartitions(ImmutableList.of("movieid"), 3).setNumReplicas(1))
    kuduContext.insertRows(predictions, modifiedTable)

    val predictionResults = sqlContext.read.options(Map("kudu.table"->modifiedTable, "kudu.master"->kuduMaster)).format("org.apache.kudu.spark.kudu").load

    predictionResults.take(10).foreach(println)

  }
}
