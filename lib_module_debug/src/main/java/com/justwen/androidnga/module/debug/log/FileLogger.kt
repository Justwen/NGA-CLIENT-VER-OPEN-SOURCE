package com.justwen.androidnga.module.debug.log

import android.util.Log
import com.justwen.androidnga.module.debug.DebugManager
import gov.anzong.androidnga.base.logger.ILogger
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLogger : ILogger {

    private val logTimeFormat = SimpleDateFormat("yyyy/MM/dd:HH:mm:ss:SSS", Locale.getDefault())

    private val fileWriter: FileWriter

    init {
        fileWriter = FileWriter(getOutputFile(), true);
    }

    private fun getOutputFile(): File {
        var logFile = File(DebugManager.debugRootDir, "log.txt")
        try {
            if (!logFile.exists()) {
                logFile.createNewFile()
            } else if (!logFile.canWrite()) {
                logFile =
                    File(DebugManager.debugRootDir, "log_" + System.currentTimeMillis() + ".txt")
                logFile.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return logFile
    }

    override fun close() {
        super.close()
        DebugManager.runDebugTask {
            fileWriter.close()
        }
    }

    override fun d(tag: String?, msg: String?) {
        super.d(tag, msg)
        DebugManager.runDebugTask {
            val time: String = logTimeFormat.format(Date())
            fileWriter.write("$time：$tag $msg")
            fileWriter.write("\n")
            fileWriter.flush()
        }
    }

    override fun d(tag: String?, msg: String?, throwable: Throwable?) {
        super.d(tag, msg, throwable)
        DebugManager.runDebugTask {
            val time: String = logTimeFormat.format(Date())
            fileWriter.write("$time：$tag $msg")
            fileWriter.write("\n")
            fileWriter.write(Log.getStackTraceString(throwable))
            fileWriter.write("\n")
            fileWriter.flush()
        }
    }
}