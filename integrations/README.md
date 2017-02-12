### Integrating Kudu with Flume and Spark

Before running these examples, I recommend installing git and maven. For example: 

    sudo yum -y install git
    wget -P /tmp/ http://apache.cs.utah.edu/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
    sudo tar xzf /tmp/apache-maven-3.3.9-bin.tar.gz -C /usr/local
    cat <<'EOF'>> maven.sh
    export M2_HOME=/usr/local/apache-maven-3.3.9
    export PATH=${M2_HOME}/bin:${PATH}
    EOF
    sudo mv maven.sh /etc/profile.d/maven.sh
    source /etc/profile
