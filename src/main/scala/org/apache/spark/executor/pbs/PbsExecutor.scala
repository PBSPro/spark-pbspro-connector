package org.apache.spark.executor.pbs

import java.net.URL

import scala.collection.mutable
import scala.sys.process._

import org.apache.spark.executor.CoarseGrainedExecutorBackend

private[pbs] object PbsExecutor {

  def main (args: Array[String]) {
    val new_args: Array[String] = "--hostname" +: s"${getHostname}" +: args
    CoarseGrainedExecutorBackend.main(new_args)
  }

  private def getHostname(): String = {
    val cmd = "hostname"
    cmd.!!.trim()
  }
}
