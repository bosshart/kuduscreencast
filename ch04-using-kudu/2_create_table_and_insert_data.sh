#!/usr/bin/env bash

#######
#
# Before getting started, you'll need to download the movielens dataset from http://files.grouplens.org/datasets/movielens/ml-100k.zip
# scp the zip file to /home/demo/ directory on kudu quickstart VM
#
#######

hadoop fs -ls /user/demo/
hadoop fs -mkdir -p /user/demo/moviedata/
hadoop fs -put user_ratings.txt /user/demo/moviedata/
hadoop fs -ls /user/demo/moviedata/


CREATE EXTERNAL TABLE user_item_ratings(
	ts INT, 
	userid INT, 
	movieid INT, 
	rating INT, 
	age INT, 
	gender STRING, 
	occupation STRING, 
	zip INT, 
	movietitle STRING, 
	releasedate STRING, 
	videoreleasedate STRING, 
	url STRING) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY "\001" STORED AS TEXTFILE LOCATION "/user/demo/moviedata/";

#Let's test selecting some data from our impala table. We're going to look at some of the ratings for one of my favorite movies: 
SELECT movieid, userid, movietitle, rating FROM user_item_ratings WHERE movietitle='Tombstone (1993)' LIMIT 10;	


#Let's create our first impala on kudu table. 

DROP TABLE IF EXISTS kudu_user_ratings;
DROP TABLE IF EXISTS kudu_user_ratings_as_select;

CREATE TABLE `kudu_user_ratings` (
`movieid` INT,
`userid` INT,
`rating` INT,
`age` INT,
`gender` STRING,
`occupation` STRING,
`zip` INT,
`movietitle` STRING,
`releasedate` STRING,
`videoreleasedate` STRING,
`url` STRING,
`ts` INT
)
DISTRIBUTE BY HASH (movieid) INTO 4 BUCKETS
TBLPROPERTIES(
  'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
  'kudu.table_name' = 'kudu_user_ratings',
  'kudu.master_addresses' = 'quickstart.cloudera:7051',
  'kudu.key_columns' = 'movieid, userid'
);
# insert a single row
INSERT INTO kudu_user_ratings VALUES (1000000, 1000000, 4, 35, 'M', 'engineer', 55409, 'Tombstone (1993)', '01-Jan-1993', null, null, 892859148);
# bulk load from HDFS into Kudu
INSERT INTO kudu_user_ratings SELECT movieid, userid, rating, age, gender, occupation, zip, movietitle, releasedate, videoreleasedate, url, ts FROM user_item_ratings;

# same thing, all in one step
CREATE TABLE kudu_user_ratings_as_select
DISTRIBUTE BY HASH (movieid) INTO 4 BUCKETS
TBLPROPERTIES(
'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
'kudu.table_name' = 'kudu_user_ratings_as_select',
'kudu.master_addresses' = '127.0.0.1',
'kudu.key_columns' = 'movieid, userid'
 ) AS SELECT movieid, userid, rating, age, gender, occupation, zip, movietitle, releasedate, videoreleasedate, url, ts  FROM user_item_ratings;