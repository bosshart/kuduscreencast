### Kudu Basics: Basic Kudu Installation, API Usage, and SQL Integration

Before running these examples, I recommend installing git and maven. See the `boostrap.sh` script or run the following: 

    sudo yum -y install git
    wget -P /tmp/ http://apache.cs.utah.edu/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
    sudo tar xzf /tmp/apache-maven-3.3.9-bin.tar.gz -C /usr/local
    cat <<'EOF'>> maven.sh
    export M2_HOME=/usr/local/apache-maven-3.3.9
    export PATH=${M2_HOME}/bin:${PATH}
    EOF
    sudo mv maven.sh /etc/profile.d/maven.sh
    source /etc/profile

You'll also want to download some sample data: 

    wget -P /tmp https://raw.githubusercontent.com/bosshart/kuduscreencast/master/api-sql-basics/user_ratings.txt

About these examples: 

1. Segment 1 demonstrates basic CRUD operations using Apache Impala and Kudu. You can run it as a script via: 
```
    git clone https://github.com/bosshart/kuduscreencast.git
    cd kuduscreencast/api-sql-basics/
    chmod u+x 2_create_table_and_insert_data.sh 
    ./2_create_table_and_insert_data.sh 
```
2. Segment 2 demonstrates querying Kudu data via Impala and depends on the steps in segment 1. You can run it as a script via: 
```    
    chmod u+x 2_create_table_and_insert_data.sh 
    ./3_query_data.sh
```
3. Run the simple API example via the following. Note that the example has the kudu master host hard-coded to be the quickstart. 
```
    mvn clean
    mvn package
    java -cp target/sample.sql-api-basics-1.0-SNAPSHOT.jar com.kuduscreencast.examples.SimpleExample
```    
    
    
    