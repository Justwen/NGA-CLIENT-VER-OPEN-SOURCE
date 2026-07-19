package gov.anzong.androidnga.base.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import gov.anzong.androidnga.common.R
import java.io.File

object ShareUtils {

    fun shareFile(context: Context, file: File) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context, context.packageName, file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            intent.setType("*/*")
            val text: String = context.resources.getString(R.string.share_to)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(intent, text))
        } catch (e: ActivityNotFoundException) {
            ToastUtils.error("分享失败！")
        }
    }


    fun shareImage(context: Context, file: File) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context, context.packageName, file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            intent.setType("image/jpeg")
            val text: String = context.resources.getString(R.string.share_to)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(intent, text))
        } catch (e: ActivityNotFoundException) {
            ToastUtils.error("分享失败！")
        }
    }

    fun shareText(context: Context, title: String, content: String) {
        try {
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, content)
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: ActivityNotFoundException) {
            ToastUtils.error("分享失败！")
        }
    }

}