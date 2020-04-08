# Spark PBSPro Support
This adds support for [PBS Professional](https://github.com/pbspro/pbspro)
HPC resource manager in [Apache Spark](https://github.com/apache/spark).


### Status of build with latest Spark
[![Build Status](https://travis-ci.com/PBSPro/spark-pbspro-connector.svg?branch=master)](https://travis-ci.com/PBSPro/spark-pbspro-connector)


## Usage

You can run Spark on the PBS cluster just by adding "--master pbs" while submitting as follows:
```bash
# start spark shell. only in client mode
./bin/spark-shell --master pbs

# submit a spark application in client mode
./bin/spark-submit --master pbs --deploy-mode client --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.1.0-SNAPSHOT.jar 100

# submit a spark application in cluster mode
./bin/spark-submit --master pbs --deploy-mode cluster --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.1.0-SNAPSHOT.jar 100
```

You can also just append `spark.master pbs` in `conf/spark-defaults.conf` to avoid adding
`--master pbs` on every submit.


To run Spark UI with PBS cluster:
```bash
bin/spark-class org.apache.spark.deploy.pbs.ui.PbsClusterUI
```



## Installation

This expects PBSPro to be installed at `/opt/pbs`.

Clone the Spark repository and move to spark folder
```bash
git clone https://github.com/apache/spark.git
cd spark
```

In the spark project root, punch in these commands:
```bash
# Clone the repo
git clone https://github.com/PBSPro/spark-pbspro-connector resource-managers/pbs

# Apply patch to spark (in the root directory).
git am resource-managers/pbs/*.patch

# Note: Spark requires javac for bulding
# Build!
build/mvn -DskipTests -Ppbs package
```

Additional information for building spark can be found here:

https://spark.apache.org/docs/latest/building-spark.html#building-spark

Add executor home to your configuration:
```bash
# in file conf/spark-defaults.conf add line:
spark.pbs.executor.home "SPARK INSTALLATION DIRECTORY PATH IN PBS MOMS"
```
