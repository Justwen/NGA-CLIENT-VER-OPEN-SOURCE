package com.justwen.androidnga.module.debug

import android.content.Context
import android.os.Environment
import com.justwen.androidnga.module.debug.log.FileLogger
import gov.anzong.androidnga.base.logger.Logger
import gov.anzong.androidnga.base.logger.ReleaseLogger
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.base.util.ShareUtils
import gov.anzong.androidnga.base.util.ToastUtils
import gov.anzong.androidnga.common.util.FileUtils
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object DebugManager {

    var debugMode: Boolean = false
        set(value) {
            field = value
            Logger.setLogger(
                if (field) {
                    FileLogger()
                } else {
                    ReleaseLogger()
                }
            )
        }

    val debugRootDir: File by lazy {
        initDebugRootDir()
    }

    private val logWriteThread = ThreadPoolExecutor(
        0, 1,
        5, TimeUnit.MINUTES,
        LinkedBlockingQueue()
    )

    private fun initDebugRootDir(): File {
        val rootDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/${ContextUtils.getContext().packageName}/debug"
        )
        val dateFormat: DateFormat = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        val rootDebugDir = File(rootDir, dateFormat.format(Date()))
        if (!rootDebugDir.exists()) {
            rootDebugDir.mkdirs()
        }
        return rootDebugDir
    }

    fun clearLogFiles() {
        runDebugTask {
            org.apache.commons.io.FileUtils.deleteDirectory(debugRootDir.parentFile)
            initDebugRootDir()
        }
    }

    fun shareDebugFile(context: Context) {
        runDebugTask {
            val zipFile = collectDebugFiles()
            if (zipFile.exists()) {
                ShareUtils.shareFile(context, zipFile)
            } else {
                ToastUtils.error("无法获取日志文件！")
            }
        }

    }

    private fun collectDebugFiles(): File {
        val targetFile = File(debugRootDir.parentFile, "debug_${debugRootDir.name}_${System.currentTimeMillis()}.zip")
        FileUtils.zipFiles(debugRootDir.absolutePath, targetFile.absolutePath)
        return targetFile
    }

    fun runDebugTask(task: () -> Unit) {
        logWriteThread.execute(task)
    }
}