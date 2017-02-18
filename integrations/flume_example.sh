#!/usr/bin/env bash
set -x
set -e

mkdir -p /home/demo/ratings/
wget -P /home/demo/ratings/ --no-check-certificate http://raw.githubusercontent.com/bosshart/kuduscreencast/master/integrations/flume_ratings.tsv

impala-shell -i localhost -d default -q "CREATE TABLE streaming_user_ratings (
movieid INT,
userid INT,
rating INT,
movietitle STRING,
PRIMARY KEY(movieid, userid)
)
PARTITION BY HASH(movieid)
PARTITIONS 4 STORED AS KUDU tblproperties('kudu.table_name'='streaming_user_ratings');"

mkdir -p /home/demo/kuduclient
cd /home/demo/kuduclient/
git clone -b branch-1.2.x https://github.com/apache/kudu.git
cd /home/demo/kuduclient/kudu/java/
mvn install -DskipTests


sudo mkdir -p /usr/lib/flume-ng/plugins.d/kudu-sink/lib
sudo cp /home/demo/kuduclient/kudu/java/kudu-flume-sink/target/kudu-flume-sink-1.2.1-SNAPSHOT.jar /usr/lib/flume-ng/plugins.d/kudu-sink/lib
sudo chown flume:flume /home/demo/kuduclient/kudu/java/kudu-flume-sink/target/kudu-flume-sink-1.2.1-SNAPSHOT.jar

echo "agent.sources  = source1
agent.channels = channel1
agent.sinks    = sink1
agent.sources.source1.type = spooldir
agent.sources.source1.spoolDir = /home/demo/ratings/
agent.sources.source1.fileHeader = false
agent.sources.source1.channels = channel1
agent.channels.channel1.type                = memory
agent.channels.channel1.capacity            = 10000
agent.channels.channel1.transactionCapacity = 1000
agent.sinks.sink1.type = org.apache.kudu.flume.sink.KuduSink
agent.sinks.sink1.masterAddresses = localhost
agent.sinks.sink1.tableName = streaming_user_ratings
agent.sinks.sink1.channel = channel1
agent.sinks.sink1.batchSize = 50
agent.sinks.sink1.producer = org.apache.kudu.flume.sink.RegexpKuduOperationsProducer
agent.sinks.sink1.producer.pattern = (?<movieid>\\\d+)\\\t(?<userid>\\\d+)\\\t(?<rating>\\\d+)\\\t(?<movietitle>.*)" | sudo tee /etc/flume-ng/conf/flume.conf




###########
# old C5.8 syntax
#CREATE TABLE `streaming_user_ratings` (movieid INT,userid INT,rating INT,movietitle STRING)
#DISTRIBUTE BY HASH (movieid) INTO 4 BUCKETS
#TBLPROPERTIES(
#  'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
#  'kudu.table_name' = 'streaming_user_ratings',
#  'kudu.master_addresses' = 'quickstart.cloudera:7051',
#  'kudu.key_columns' = 'movieid, userid'
#);
#############



