#!/bin/sh

spark_repo="https://github.com/apache/spark"
pbs_repo="https://github.com/PBSPro/spark-pbspro-connector"

cleanup() {
  cd /
  rm -rf /tmp/spark
}

quit() {
  echo "$1"
  cleanup
  exit 1
}


# clone spark
cd /tmp
git clone $spark_repo                       || quit "Unable to clone spark repo"
cd spark

# clone pbs
git clone $pbs_repo resource-managers/pbs   || quit "Unable to clone pbs repo"

# apply patches
git am resource-managers/pbs/*.patch        || quit "Unable to apply patch to spark"

# build
export MAVEN_SKIP_RC=1
build/mvn -q -DskipTests -Ppbs package      || quit "Unable to build spark with pbs"

cleanup
