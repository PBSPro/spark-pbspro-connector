package org.apache.spark.scheduler.cluster.pbs

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend
import org.apache.spark.internal.Logging

import org.apache.spark.executor.pbs.PbsExecutorInfo

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sparkContext: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(
    scheduler.asInstanceOf[TaskSchedulerImpl],
    sparkContext.env.rpcEnv) with Logging {

  private val driver = new PbsSchedulerDriver(sparkContext)

  /* TODO: Get this from conf or something */
  private val initialExecutors = 2

  /**
   * Start the scheduler and initialize dependent components.
   */
  override def start() {
    logInfo("Starting PBS Scheduler backend")
    super.start()
    driver.init()
    logInfo("Starting initial " + initialExecutors + " executors")
    PbsSchedulerUtils.startExecutors(sparkContext, initialExecutors)
  }

  /**
   * Stop the scheduler driver.
   */
  override def stop() {
    logInfo("Stopping PBS Scheduler backend")
  }

  /**
   * Check if we have enough for our need (not for want... we can never have enough for our want).
   *
   * @return true if enough resources registered
   */
  override def sufficientResourcesRegistered(): Boolean = {
    logInfo("sufficientResourcesRegistered")
    /* TODO */
    true
  }

  /**
   * Kill the executors in the given list.
   *
   * @param executorIds list of Executor IDs to kill
   * @return if all the Executors were killed
   */
  override def doKillExecutors(executorIds: Seq[String]): Future[Boolean] = Future.successful {
    for (executor <- executorIds) {
      logInfo("Killing executor: " + executor)
      //executorDriver.kill(executor)
    }
    true
  }

  /**
   * Notify the scheduler about the number of Executors wanted by the application. Start up
   * Executors (ExecutorBackend) on PBS, which will in turn register themselves with the
   * SchedulerDriver.
   *
   * TODO: Change it to use threads to launch executors
   *
   * @param requestedTotal number of Executors wanted (including already allocated)
   * @return if the request is acknowledged
   */
  override def doRequestTotalExecutors(requestedTotal: Int): Future[Boolean] = Future.successful {
    logInfo(requestedTotal + " executors requested")
    PbsSchedulerUtils.startExecutors(sparkContext, requestedTotal)
  }

}
