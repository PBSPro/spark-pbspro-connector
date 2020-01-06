#!/bin/sh


if [ -z "$TRAVIS_BUILD_DIR" ]; then
    build_dir=$(pwd)
else
    build_dir=$TRAVIS_BUILD_DIR
fi

# clone spark project
git clone $spark_repo $spark_dir
cd $spark_dir

# setup pbs connector in spark project
mv $build_dir $spark_dir/resource-managers/pbs
cd $spark_dir/resource-managers/pbs
