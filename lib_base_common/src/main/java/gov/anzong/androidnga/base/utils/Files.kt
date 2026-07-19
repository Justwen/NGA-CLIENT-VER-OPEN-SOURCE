package gov.anzong.androidnga.base.utils

import android.content.Context
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

object Files {

    fun delete(file: File): Boolean {
        return FileUtils.deleteQuietly(file)
    }

    fun readAssetString(context: Context, path: String): String {
        try {
            context.assets.open(path).use {
                val length = it.available()
                val buffer = ByteArray(length)
                it.read(buffer)
                return String(buffer, Charsets.UTF_8)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    fun readFile(file: File): String {
        return FileUtils.readFileToString(file, Charset.defaultCharset())
    }

    fun writeFile(file: File, data: String) {
        FileUtils.write(file, data, Charset.defaultCharset())
    }

}