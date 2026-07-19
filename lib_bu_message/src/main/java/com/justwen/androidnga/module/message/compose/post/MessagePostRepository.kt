package com.justwen.androidnga.module.message.compose.post

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import java.net.URLEncoder

object MessagePostRepository {

    private val SUCCESS_TAG = arrayOf("发送完毕 ...", " @提醒每24小时不能超过50个", "操作成功")

    private val queryParamMap: HashMap<String, String> =
        hashMapOf("__lib" to "message", "__act" to "message", "lite" to "js", "charset" to "gbk")

    suspend fun post(data: MessagePostData): Result<String> {
        return try {
            val netService = RetrofitHelper.getInstance()
                .getService(RetrofitServiceKt::class.java) as RetrofitServiceKt
            val result = netService.post(queryParamMap, buildBody(data))
            checkResult(result);
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildBody(data: MessagePostData): Map<String, String> {
        queryParamMap["act"] = data.action

        val fieldParamMap = HashMap(queryParamMap)

        fieldParamMap["mid"] = data.mid
        var recipient = data.recipient.replace("，".toRegex(), ",");
        fieldParamMap["to"] = URLEncoder.encode(recipient, "gbk")
        if (data.postSubject.isNotEmpty()) {
            fieldParamMap["subject"] = URLEncoder.encode(data.postSubject, "gbk")
        }
        fieldParamMap["content"] = URLEncoder.encode(data.postContent, "gbk")
        return fieldParamMap
    }

    private fun checkResult(result: String): Result<String> {
        var js = result.replace("window.script_muti_get_var_store=".toRegex(), "")
        if (js.indexOf("/*error fill content") > 0) {
            js = js.substring(0, js.indexOf("/*error fill content"))
        }

        js = js.replace("\"content\":\\+(\\d+),".toRegex(), "\"content\":\"+$1\",")
            .replace("\"subject\":\\+(\\d+),".toRegex(), "\"subject\":\"+$1\",")
            .replace("/\\*\\\$js\\$\\*/".toRegex(), "")

        var jsonObject = JSON.parseObject(js)["data"] as JSONObject?
        if (jsonObject == null) {
            jsonObject = JSON.parseObject(js)["error"] as JSONObject?
            jsonObject?.let {
                val errorMsg = jsonObject.getString("0")
                return Result.failure(Throwable(errorMsg))
            }
        } else {
            val msg = jsonObject.getString("0")
            if (SUCCESS_TAG.contains(msg)) {
                return Result.success(msg)
            }
        }

        return Result.failure(Throwable("发送失败！"))
    }
}