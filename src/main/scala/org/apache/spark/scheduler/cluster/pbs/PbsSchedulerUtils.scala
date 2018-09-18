package org.apache.spark.scheduler.cluster.pbs

import java.io.File

import scala.sys.process._

import org.apache.spark.rpc.RpcEndpointAddress
import org.apache.spark.SparkContext
import org.apache.spark.executor.pbs.PbsExecutorInfo

import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend

private[pbs] object PbsSchedulerUtils {

  private var last_task = 0

  /**
   * Start an Executor.
   *
   * @param sparkContext spark application
   * @return Executor info for the started executor
   */
  private def newExecutor(sparkContext: SparkContext): Option[PbsExecutorInfo] = {
    val driverUrl = RpcEndpointAddress(
      sparkContext.conf.get("spark.driver.host"),
      sparkContext.conf.get("spark.driver.port").toInt,
      CoarseGrainedSchedulerBackend.ENDPOINT_NAME).toString

    val environ = ""                                        // TODO
    val executorHostname = "ubuntu"                         // TODO
    val taskId = last_task                                  // TODO
    last_task += 1
    val appId = "APPID"                                     // TODO
    val numCores = sparkContext.conf.getOption("spark.executor.cores").getOrElse(3)
    val sparkHome = sparkContext.getSparkHome() match {
      case Some(home) =>
        home
      case None =>
        "/home/utkmah/code/spark/" // FIXME
    }

    val runScript = new File(sparkHome, "bin/spark-class").getPath

    val opts = s"--driver-url $driverUrl" +
        //s" --hostname $executorHostname" +
        s" --cores $numCores" +
        s" --app-id $appId" +
        //s" --worker-url ubuntu:13245" +
        //s" --user-class-path file://" +
        s" --executor-id $taskId"

    val command = s"$runScript org.apache.spark.executor.pbs.PbsExecutor $opts"

    val cmd = s"/opt/pbs/bin/qsub -N spark-job-$appId-$taskId -l select=1:ncpus=$numCores -- $command"
    val out = cmd.!!
    None
  }

  /**
   * Start Executors such that there is a specific number of total executors running.
   *
   * @param sparkContext spark application
   * @param totalExecutors total number of executors wanted (including already running)
   */
  def startExecutors(sparkContext: SparkContext, totalExecutors: Int): Boolean = {
    // FIXME: Check for already running executors
    for (i <- 1 to totalExecutors) {
      newExecutor(sparkContext) match {
        case Some(executor: PbsExecutorInfo) =>
        case None =>
      }
    }
    true
  }

}
