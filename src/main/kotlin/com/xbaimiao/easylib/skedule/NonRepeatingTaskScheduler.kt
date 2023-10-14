package com.xbaimiao.easylib.skedule

import com.xbaimiao.easylib.skedule.SynchronizationContext.ASYNC
import com.xbaimiao.easylib.skedule.SynchronizationContext.SYNC
import com.xbaimiao.easylib.task.EasyLibTask

class NonRepeatingTaskScheduler(private val scheduler: EasyScheduler) : TaskScheduler {

    override var currentTask: EasyLibTask? = null

    override fun doWait(ticks: Long, task: (Long) -> Unit) {
        runTaskLater(ticks) { task(ticks) }
    }

    override fun <T> doAsync(asyncFunc: () -> T, task: (T) -> Unit) {
        val currentContext = currentContext()
        runTask(ASYNC) {
            val result = asyncFunc()
            if (currentContext == ASYNC) {
                task(result)
            } else {
                runTask(SYNC) {
                    task(result)
                }
            }
        }
    }

    override fun <T> doSync(syncFunc: () -> T, task: (T) -> Unit) {
        val currentContext = currentContext()
        runTask(SYNC) {
            val result = syncFunc()
            if (currentContext == SYNC) {
                task(result)
            } else {
                runTask(currentContext) {
                    task(result)
                }
            }
        }
    }

    override fun doContextSwitch(context: SynchronizationContext, task: (Boolean) -> Unit) {
        val currentContext = currentContext()
        if (context == currentContext) {
            task(false)
        } else {
            forceNewContext(context) { task(true) }
        }
    }

    override fun forceNewContext(context: SynchronizationContext, task: () -> Unit) {
        runTask(context) { task() }
    }

    private fun runTask(context: SynchronizationContext = currentContext(), task: () -> Unit) {
        currentTask = when (context) {
            SYNC -> scheduler.runTask(task)
            ASYNC -> scheduler.runTaskAsynchronously(task)
        }
    }

    private fun runTaskLater(ticks: Long, context: SynchronizationContext = currentContext(), task: () -> Unit) {
        currentTask = when (context) {
            SYNC -> scheduler.runTaskLater(task, ticks)
            ASYNC -> scheduler.runTaskLaterAsynchronously(task, ticks)
        }
    }

}