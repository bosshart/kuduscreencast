#!/usr/bin/env bash

set -x
set -e

sudo echo "[cloudera-kafka]
# Packages for Cloudera's Distribution for kafka, Version 2, on RedHat  or CentOS 6 x86_64
name=Cloudera's Distribution for kafka, Version 2
baseurl=http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/2.0.1/
gpgkey = http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/RPM-GPG-KEY-cloudera
gpgcheck = 1" > /etc/yum.repos.d/cloudera-kafka.repo

sudo echo "[cloudera-cdh5]
# Packages for Cloudera's Distribution for Hadoop, Version 5, on RedHat	or CentOS 6 x86_64
name=Cloudera's Distribution for Hadoop, Version 5
baseurl=http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/5.10/
gpgkey = http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera
gpgcheck = 1" > /etc/yum.repos.d/cloudera-cdh5.repo

sudo yum -y install git
sudo yum -y install zookeeper-server
mkdir -p /var/lib/zookeeper
sudo chown -R zookeeper /var/lib/zookeeper/
sudo service zookeeper-server init
sudo service zookeeper-server start

sudo yum -y install kafka kafka-server
sudo yum -y install spark-core spark-master spark-worker

#Replace the relevant portion of /etc/spark/conf/spark-env.sh to point to the host where the spark-master runs
sed -i "s/export STANDALONE_SPARK_MASTER_HOST=\`hostname\`/export STANDALONE_SPARK_MASTER_HOST=quickstart.cloudera/" /etc/spark/conf/spark-env.sh

sudo service kafka-server start
sudo service spark-master start
sudo service spark-worker start

wget -P /tmp/ http://apache.cs.utah.edu/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
sudo tar xzf /tmp/apache-maven-3.3.9-bin.tar.gz -C /usr/local
cat <<'EOF'>> maven.sh
export M2_HOME=/usr/local/apache-maven-3.3.9
export PATH=${M2_HOME}/bin:${PATH}
EOF
sudo mv maven.sh /etc/profile.d/maven.sh
source /etc/profile
