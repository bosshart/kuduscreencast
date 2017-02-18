#!/usr/bin/env bash
set -x
set -e


wget -P /tmp/ --no-check-certificate https://raw.githubusercontent.com/bosshart/kuduscreencast/master/api-sql-basics/user_ratings.txt
hadoop fs -ls /user/demo/
hadoop fs -mkdir -p /user/demo/moviedata/
hadoop fs -put /tmp/user_ratings.txt /user/demo/moviedata/

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


impala-shell -i localhost -d default -q "CREATE TABLE kudu_user_ratings PRIMARY KEY(movieid,userid)
PARTITION BY HASH(movieid)
PARTITIONS 4 STORED AS KUDU tblproperties('kudu.table_name'='kudu_user_ratings')
AS SELECT movieid, userid, rating, age, gender, occupation, zip, movietitle, releasedate, videoreleasedate, url, ts  FROM user_item_ratings;"

sudo echo "[cloudera-cdh5]
# Packages for Cloudera's Distribution for Hadoop, Version 5, on RedHat	or CentOS 6 x86_64
name=Cloudera's Distribution for Hadoop, Version 5
baseurl=http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/5.10/
gpgkey = http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera
gpgcheck = 1" > /etc/yum.repos.d/cloudera-cdh5.repo

# Need all kinds of dependencies to build the kudu client and flume jar
sudo yum -y install git flume-ng flume-ng-agent autoconf automake libtool gcc gcc-c++
wget -P /tmp/ https://github.com/google/protobuf/releases/download/v2.6.1/protobuf-2.6.1.tar.gz -O /tmp/protobuf-2.6.1.tar.gz
tar -xvf /tmp/protobuf-2.6.1.tar.gz -C /tmp/
cd /tmp/protobuf-2.6.1/
./configure
make
sudo make install

sudo service flume-ng-agent start
sudo service flume-ng-agent status
wget -P /tmp/ http://apache.cs.utah.edu/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
sudo tar xzf /tmp/apache-maven-3.3.9-bin.tar.gz -C /usr/local
cat <<'EOF'>> maven.sh
export M2_HOME=/usr/local/apache-maven-3.3.9
export PATH=${M2_HOME}/bin:${PATH}
EOF
sudo mv maven.sh /etc/profile.d/maven.sh
source /etc/profile

