package org.apache.spark.executor.pbs

import java.nio.ByteBuffer
import java.net.URL

import org.apache.spark.{SparkConf, SparkEnv, TaskState}
import org.apache.spark.internal.Logging
import org.apache.spark.rpc._
import org.apache.spark.executor.CoarseGrainedExecutorBackend

private[pbs] class PbsCoarseGrainedExecutorBackend(
    rpcEnv: RpcEnv,
    driverUrl: String,
    executorId: String,
    hostname: String,
    cores: Int,
    userClassPath: Seq[URL],
    env: SparkEnv)
  extends CoarseGrainedExecutorBackend(
    rpcEnv,
    driverUrl,
    executorId,
    hostname,
    cores,
    userClassPath,
    env) {

  //override def statusUpdate(taskId: Long, state: TaskState.TaskState, data: ByteBuffer) {
  //}

}

/*
 * TODO: Mesos has an object which is the "entry point" for executor, as follows:
 *    private[spark] object MesosExecutorBackend extends Logging {
 *
 * Find out what has to be done with this shit.
 */
