#!/usr/bin/env bash

# new
sudo echo "[cloudera-cdh5]
# Packages for Cloudera's Distribution for Hadoop, Version 5, on RedHat	or CentOS 6 x86_64
name=Cloudera's Distribution for Hadoop, Version 5
baseurl=http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/5.10/
gpgkey = http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera
gpgcheck = 1" > /etc/yum.repos.d/cloudera-cdh5.repo

sudo yum -y install git flume-ng flume-ng-agent
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
