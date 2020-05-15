/*
 * Copyright (C) 1994-2019 Altair Engineering, Inc.
 * For more information, contact Altair at www.altair.com.
 *
 * This file is part of the PBS Professional ("PBS Pro") software.
 *
 * Open Source License Information:
 *
 * PBS Pro is free software. You can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * PBS Pro is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Commercial License Information:
 *
 * For a copy of the commercial license terms and conditions,
 * go to: (http://www.pbspro.com/UserArea/agreement.html)
 * or contact the Altair Legal Department.
 *
 * Altair’s dual-license business model allows companies, individuals, and
 * organizations to create proprietary derivative works of PBS Pro and
 * distribute them - whether embedded or bundled with other software -
 * under a commercial license agreement.
 *
 * Use of Altair’s trademarks, including but not limited to "PBS™",
 * "PBS Professional®", and "PBS Pro™" and Altair’s logos is subject to Altair's
 * trademark licensing policies.
 *
 */

package org.apache.spark.scheduler.cluster.pbs

import scala.concurrent.Future

import org.apache.spark.{SecurityManager, SparkConf, SparkContext}
import org.apache.spark.internal.Logging
import org.apache.spark.executor.pbs.PbsExecutorInfo
import org.apache.spark.resource.ResourceProfile
import org.apache.spark.scheduler.{TaskScheduler, TaskSchedulerImpl}
import org.apache.spark.scheduler.cluster.{CoarseGrainedSchedulerBackend, SchedulerBackendUtils}

private[spark] class PbsCoarseGrainedSchedulerBackend(
    scheduler: TaskScheduler,
    sparkContext: SparkContext,
    masterURL: String)
  extends CoarseGrainedSchedulerBackend(
    scheduler.asInstanceOf[TaskSchedulerImpl],
    sparkContext.env.rpcEnv) with Logging {

  private val initialExecutors: Int = SchedulerBackendUtils.getInitialTargetExecutorNumber(conf)
  private val defaultMinRegisteredRatio: Double = 0.8

  protected override val minRegisteredRatio: Double = {
    if (conf.getOption("spark.scheduler.minRegisteredResourcesRatio").isEmpty) {
      defaultMinRegisteredRatio
    } else {
      super.minRegisteredRatio
    }
  }

  /**
   * Start the scheduler and initialize dependent components.
   */
  override def start() {
    logInfo("Starting PBS Scheduler backend")
    super.start()
    logInfo("Starting initial " + initialExecutors + " executors")
    PbsSchedulerUtils.startExecutors(sparkContext, initialExecutors)
  }

  /**
   * Stop the scheduler driver.
   */
  override def stop() {
    logInfo("Stopping PBS Scheduler backend")
    super.stop()
  }

  /**
   * Check if we have enough for our need (not for want... we can never have enough for our want).
   *
   * @return true if enough resources registered
   */
  override def sufficientResourcesRegistered(): Boolean = {
    totalRegisteredExecutors.get() >= initialExecutors * minRegisteredRatio
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
      // executorDriver.kill(executor)
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
  override def doRequestTotalExecutors(
      resourceProfileToTotalExecs: Map[ResourceProfile, Int]): Future[Boolean] = Future.successful {
    //logInfo(requestedTotal + " executors requested")
    //logInfo(resourceProfileToTotalExecs(defaultProfile) + " executors requested")
    PbsSchedulerUtils.startExecutors(sparkContext, 2)
  }

}
