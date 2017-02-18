#!/usr/bin/env bash
set -x
set -e
#######
#
# Before getting started, you'll need to download the dataset from https://github.com/bosshart/kuduscreencast/raw/master/api-sql-basics/user_ratings.txt
# scp the zip file to /home/demo/ directory on kudu quickstart VM
# Or download: wget https://raw.githubusercontent.com/bosshart/kuduscreencast/master/api-sql-basics/user_ratings.txt
#######

hadoop fs -ls /user/demo/
hadoop fs -mkdir -p /user/demo/moviedata/
hadoop fs -put /tmp/user_ratings.txt /user/demo/moviedata/
hadoop fs -ls /user/demo/moviedata/


impala-shell -i localhost -d default -q "CREATE EXTERNAL TABLE user_item_ratings(
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001' STORED AS TEXTFILE LOCATION '/user/demo/moviedata/';"

#Let's test selecting some data from our impala table. We're going to look at some of the ratings for one of my favorite movies: 
impala-shell -i localhost -d default -q " SELECT movieid, userid, movietitle, rating FROM user_item_ratings WHERE movietitle='Tombstone (1993)' LIMIT 10;"


#Let's create our first impala on kudu table. 

impala-shell -i localhost -d default -q "DROP TABLE IF EXISTS kudu_user_ratings;"
impala-shell -i localhost -d default -q "DROP TABLE IF EXISTS kudu_user_ratings_as_select;"

impala-shell -i localhost -d default -q "CREATE TABLE kudu_user_ratings (
movieid INT,
userid INT,
rating INT,
age INT,
gender STRING,
occupation STRING,
zip INT,
movietitle STRING,
releasedate STRING,
videoreleasedate STRING,
url STRING,
ts INT,
PRIMARY KEY(movieid,userid)
)
PARTITION BY HASH(movieid)
PARTITIONS 4 STORED AS KUDU;"

# insert a single row
impala-shell -i localhost -d default -q "INSERT INTO kudu_user_ratings VALUES (1000000, 1000000, 4, 35, 'M', 'engineer', 55409, 'Tombstone (1993)', '01-Jan-1993', null, null, 892859148);"
# bulk load from HDFS into Kudu
impala-shell -i localhost -d default -q "INSERT INTO kudu_user_ratings SELECT movieid, userid, rating, age, gender, occupation, zip, movietitle, releasedate, videoreleasedate, url, ts FROM user_item_ratings;"

# same thing, all in one step
impala-shell -i localhost -d default -q "CREATE TABLE kudu_user_ratings_as_select PRIMARY KEY(movieid,userid)
PARTITION BY HASH(movieid)
PARTITIONS 4 STORED AS KUDU
AS SELECT movieid, userid, rating, age, gender, occupation, zip, movietitle, releasedate, videoreleasedate, url, ts  FROM user_item_ratings;"