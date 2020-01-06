#!/bin/sh

# go to spark root
cd ../../

# apply patches
git am resource-managers/pbs/*.patch

# build
export MAVEN_SKIP_RC=1
build/mvn -q -DskipTests -Ppbs package
