#!/usr/bin/env bash

#######
#
# Before getting started, you'll need to download the movielens dataset from http://files.grouplens.org/datasets/movielens/ml-100k.zip
# scp the zip file to /home/demo/ directory on kudu quickstart VM
#
#######


#put this file
set -x
set -e
echo "Setting up env"
CURRENT_USER=demo
DATABASE_NAME=demo_db


mkdir -p /home/demo/moviedata
wget -P /home/demo/moviedata http://files.grouplens.org/datasets/movielens/ml-100k.zip
unzip /home/demo/moviedata/ml-100k.zip -d /home/demo/moviedata
# comma is an easier delimiter for people to understand, convert to comma separated values
awk '{ print $4 "," $1 "," $2 "," $3 }' /home/demo/moviedata/ml-100k/u.data | sort > /home/demo/moviedata/ml-100k/ratings.csv


# create directories for our data in HDFS
hadoop fs -mkdir -p /user/$CURRENT_USER/moviedata/raw/ratings/
# copy the data from the local filesystem into HDFS
hadoop fs -put /home/demo/moviedata/ml-100k/ratings.csv /user/$CURRENT_USER/moviedata/raw/ratings/

