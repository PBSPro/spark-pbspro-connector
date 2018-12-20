/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.deploy.pbs

import java.io.File

import org.apache.spark.{SparkConf, SparkException}
import org.apache.spark.internal.Logging
import org.apache.spark.pbs.Utils

private[pbs] class Client(args: ClientArguments, sparkConf: SparkConf) extends Logging {

  private val defaultDriverCores: Int = 1
  private val defaultDriverMemory: String = "1G"
  private val defaultAppName: String = "spark-driver"

  /**
   * This is responsible to build and start a driver program on a node in the cluster. This driver
   * then builds the SparkContext and starts the application to which other Executors connect.
   *
   * Our objective is to get the spark-submit to a node in the cluster. This can be achieved if we
   * simply qsub the spark-submit command, but in client mode so that the driver is started on
   * whatever node the qsub went for execution. The driver which will be started will handle the
   * launching of executors on its own.
   */
  def run() {
    // this is only called when in cluster mode which means the spark home should be the executor's
    // spark home
    val sparkHome: String = sparkConf.getOption("spark.pbs.executor.home")
      .orElse(sparkConf.getOption("spark.home"))
      .getOrElse {
        throw new SparkException("Executor Spark home `spark.pbs.executor.home` not set!")
      }
    val appName: String = "sparkjob-" + sparkConf.getOption("spark.app.name")
      .getOrElse(defaultAppName)
      .replaceAll("\\s", "")
      .replaceAll("\\.", "-")
    val driverCores: Int = sparkConf.getOption("spark.driver.cores") match {
      case Some(cores) => cores.toInt
      case None => defaultDriverCores
    }
    val driverMemory: String = sparkConf.getOption("spark.driver.memory")
      .getOrElse(defaultDriverMemory)
    val runScript: String = new File(sparkHome, "bin/spark-submit").getPath

    val opts: String = "--master pbs --deploy-mode client" +
        s" --driver-cores $driverCores --driver-memory $driverMemory" +
        s" --class ${args.mainClass} ${args.primaryJavaResource} ${args.arg}"
        s"" // TODO: Configure the driver command for python, R etc.

    val driverCommand = s"$runScript $opts"

    logInfo(s"Starting driver: $driverCommand")
    Utils.qsub(appName, driverCores, driverMemory, driverCommand)
  }
}
