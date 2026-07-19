package gov.anzong.androidnga.base.utils

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadProvider {

    private val singleThread = ThreadPoolExecutor(
        0,
        1,
        1,
        TimeUnit.MINUTES,
        LinkedBlockingQueue<Runnable>(1024)
    )

    fun runOnUiThread(runnable: Runnable) {
        runnable.run()
    }

    fun runOnSingleThread(runnable: Runnable) {
        singleThread.execute(runnable)
    }
}