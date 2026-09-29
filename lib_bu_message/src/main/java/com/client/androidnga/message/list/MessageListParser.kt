package com.client.androidnga.message.list

import com.alibaba.fastjson2.JSONObject
import gov.anzong.androidnga.base.util.StringUtils
import gov.anzong.androidnga.common.base.JavaBean

class MessageListParser {

    fun parse(js: String?): Result<MessageListInfo> {
        if (js.isNullOrEmpty()) {
            return Result.failure(Exception("网络错误,响应内容为空"))
        }
        val result = js.removePrefix("window.script_muti_get_var_store=")
        return try {
            parseInternal(result)
        } catch (e: Exception) {
            Result.failure(Exception("消息列表解析失败", e))
        }
    }

    private fun parseInternal(result: String): Result<MessageListInfo> {
        val obj: JSONObject =
            JSONObject.parseObject(result) ?: return Result.failure(Exception("数据格式错误"))

        val dataObj = obj.getJSONObject("data")?.getJSONObject("0")
        dataObj?.let {
            val messageInfo = MessageListInfo().apply {
                rowsPerPage = it.getIntValue("rowsPerPage")
                nextPage = it.getIntValue("nextPage")
                currentPage = it.getIntValue("currentPage")
            }
            var i = 0
            var rowObj: JSONObject? = it.getJSONObject(i.toString())
            while (rowObj != null) {
                parseMessageListItem(rowObj)?.let { item ->
                    messageInfo.messageItemList.add(item)
                }
                i++
                rowObj = it.getJSONObject(i.toString())
            }
            return Result.success(messageInfo)
        }
        val errorObj = obj.getJSONObject("error")
        errorObj?.let {
            val errorStr = it.getString("0")
            if (!errorStr.isNullOrEmpty()) {
                return Result.failure(Exception(errorStr))
            }
        }
        return Result.failure(Exception("未知错误"))
    }

    private fun parseMessageListItem(rowObj: JSONObject): MessageListItem? {
        try {
            val objBean = rowObj.toJavaObject(MessageListBean::class.java)
            val messageItem = MessageListItem().apply {
                mid = objBean.mid
                subject = objBean.subject
                posts = objBean.posts
                fromUsername = objBean.from_username
                lastFromUsername = objBean.last_from_username
                time = objBean.time.takeIf { it.isNotEmpty() }?.let {
                    StringUtils.timeStamp2Date1(it)
                }.orEmpty()
                lastTime = objBean.last_modify.takeIf { it.isNotEmpty() }?.let {
                    StringUtils.timeStamp2Date1(it)
                }.orEmpty()
            }
            return messageItem
        } catch (e: Exception) {
            return null
        }
    }
}

private data class MessageListBean(
    val all_user: String = "",
    val bit: Int = 0,
    val from: Int = 0,
    val from_user_bit: Int = 0,
    val from_username: String = "",
    val last_from: Int = 0,
    val last_from_username: String = "",
    val last_modify: String = "",
    val mid: Int = 0,
    val posts: Int = 0,
    val sbit: Int = 0,
    val subject: String = "",
    val time: String = ""
) : JavaBean