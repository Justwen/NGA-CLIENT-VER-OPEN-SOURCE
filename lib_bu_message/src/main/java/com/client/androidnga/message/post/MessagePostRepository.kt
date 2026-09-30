package com.client.androidnga.message.post

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import kotlinx.coroutines.CancellationException
import java.net.URLEncoder

class MessagePostRepository {

    private val SUCCESS_TAG = arrayOf("发送完毕 ...", " @提醒每24小时不能超过50个", "操作成功")

    private val queryParamMap: HashMap<String, String> =
        hashMapOf("__lib" to "message", "__act" to "message", "lite" to "js", "charset" to "gbk")

    suspend fun post(data: MessagePostUiState): Result<String> {
        return try {
            val netService = RetrofitHelper.getInstance()
                .getService(RetrofitServiceKt::class.java) as RetrofitServiceKt
            val result = netService.post(queryParamMap, buildBody(data))
            checkResult(result);
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildBody(data: MessagePostUiState): Map<String, String> {
        queryParamMap["act"] = data.mode

        val fieldParamMap = HashMap(queryParamMap)

        fieldParamMap["mid"] = data.mid
        val recipient = data.recipient.replace("，".toRegex(), ",");
        fieldParamMap["to"] = URLEncoder.encode(recipient, "gbk")
        if (data.subject.isNotEmpty()) {
            fieldParamMap["subject"] = URLEncoder.encode(data.subject, "gbk")
        }
        fieldParamMap["content"] = URLEncoder.encode(data.content, "gbk")
        return fieldParamMap
    }

    private fun checkResult(result: String): Result<String> {
        var js = result.replace("window.script_muti_get_var_store=".toRegex(), "")
        if (js.indexOf("/*error fill content") > 0) {
            js = js.substringBefore("/*error fill content")
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