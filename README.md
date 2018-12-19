# Spark PBSPro Support
This adds support for [PBS Professional](https://github.com/pbspro/pbspro)
HPC resource manager in [Apache Spark](https://github.com/apache/spark).


### Status of build with latest Spark
[![Build Status](https://travis-ci.org/sakshamgarg/Spark-PBSPro.svg?branch=master)](https://travis-ci.org/sakshamgarg/Spark-PBSPro)


## Usage

You can run Spark on the PBS cluster just by adding "--master pbs" while submitting as follows:
```bash
# start spark shell. only in client mode
./bin/spark-shell --master pbs

# submit a spark application in client mode
./bin/spark-submit --master pbs --deploy-mode client --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.0.0-SNAPSHOT.jar 100

# submit a spark application in cluster mode
./bin/spark-submit --master pbs --deploy-mode cluster --class org.apache.spark.examples.SparkPi $SPARK_HOME/examples/target/scala-2.12/jars/spark-examples_2.12-3.0.0-SNAPSHOT.jar 100
```

You can also just append `spark.master pbs` in `conf/spark-defaults.conf` to avoid adding
`--master pbs` on every submit.



## Installation

This expects PBSPro to be installed at `/opt/pbs`.

In the spark project root, punch in these commands:
```bash
# Clone the repo
git clone https://github.com/UtkarshMe/Spark-PBSPro resource-managers/pbs

# Apply patch to spark (in the root directory).
git am resource-managers/pbs/*.patch

# Build!
build/mvn -DskipTests -Ppbs package
```

Add executor home to your configuration:
```bash
# in file conf/spark-defaults.conf add line:
spark.pbs.executor.home "SPARK INSTALLATION DIRECTORY PATH IN PBS MOMS"
```
