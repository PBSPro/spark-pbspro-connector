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

Optional: See installation step appending `spark.master pbs` in `conf/spark-defaults.conf` to avoid adding `--master pbs` on every submit.


To run Spark UI with PBS cluster:
```bash
bin/spark-class org.apache.spark.deploy.pbs.ui.PbsClusterUI
```


## Installation

### Requirements
1. PBS Professional must be installed in default locaiton, at `/opt/pbs`.
2. Confirm host resources (CPU, Memory, and Disk) are sufficient for Spark project and build. More information about these resource requirements can be found at https://spark.apache.org/docs/latest/hardware-provisioning.html
3. Spark project must be accessible by the submission host (PBS Client) and execution host(s) (PBS MoM), which is consistent on all hosts. 


### Steps
Clone the Spark repository to the host
```bash
git clone https://github.com/apache/spark.git
```

Change directory to spark folder
```bash
cd spark
```

Execute these commands:
```bash
# Clone the repo
git clone https://github.com/PBSPro/spark-pbspro-connector resource-managers/pbs

# Apply patch to spark (in the root directory).
git am resource-managers/pbs/*.patch

# Build!
build/mvn -DskipTests -Ppbs package
```

Update `conf/sparks-default.conf` by adding executor home to your configuration:
```bash
# in file conf/spark-defaults.conf add line:
spark.pbs.executor.home /common/path/to/spark/folder/on/hosts
```

Optional: Append `spark.master pbs` in `conf/spark-defaults.conf` to avoid adding `--master pbs` on every submit.
```bash
# in file conf/spark-defaults.conf:
spark.pbs.executor.home /common/path/to/spark/folder/on/hosts
spark.master pbs
```
