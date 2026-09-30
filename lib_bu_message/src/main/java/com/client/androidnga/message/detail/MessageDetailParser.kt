package com.client.androidnga.message.detail

import com.alibaba.fastjson2.JSONObject
import com.client.androidnga.message.R
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.base.util.StringUtils

/**
 * Created by Justwen on 2017/10/10.
 */
class MessageDetailParser {

    fun parse(js: String?): Result<MessageDetailInfo> {
        var js = js
        if (js.isNullOrEmpty()) {
            return Result.failure(exception = Exception(ContextUtils.getString(R.string.network_error)))
        }
        js = js.removePrefix("window.script_muti_get_var_store=")
        val obj = JSONObject.parseObject(js) ?: return Result.failure(
            exception = Exception(
                ContextUtils.getString(R.string.network_error)
            )
        )

        obj.getJSONObject("data")?.getJSONObject("0")?.let {
            return Result.success(parseDetailInfo(it))
        }

        val errorMsg = obj.getJSONObject("error")?.getString("0")
            ?: ContextUtils.getString(R.string.network_error)
        return Result.failure(Exception(errorMsg))

    }

    private fun parseDetailInfo(jsObj: JSONObject): MessageDetailInfo {
        return MessageDetailInfo().apply {
            currentPage = jsObj.getIntValue("currentPage")
            nextPage = jsObj.getIntValue("nextPage")
            messageEntryList = mutableListOf()

            val userInfoMap = jsObj.getJSONObject("userInfo")

            jsObj.getJSONObject("allmsgs")?.let { it ->
                var i = 0
                var rowObj: JSONObject? = it.getJSONObject(i.toString())
                while (rowObj != null) {
                    messageEntryList.add(MessageDetailItem().apply {
                        subject = rowObj.getString("subject")
                        lou = 20 * (currentPage - 1) + i
                        rowObj.getIntValue("time").takeIf({ time -> time > 0 }).let {
                            time = StringUtils.timeStamp2Date1(it.toString())
                        }
                        author = userInfoMap?.getJSONObject(rowObj.getString("from"))
                            ?.getString("username") ?: ""
                        content = StringUtils.replaceAll(
                            rowObj.getString("content"),
                            "\\[quote\\](.+?)\\[/quote\\]",
                            "\n$1\n"
                        ).replace("<br/>".toRegex(), "\n")
                        contentSections = parseMessageContent(content)

                    })
                    i++
                    rowObj = it.getJSONObject(i.toString())
                }

            }


        }

    }

    private fun parseMessageContent(content: String): List<Pair<String, Boolean>> {
        val contentSections = mutableListOf<Pair<String, Boolean>>()
        for (section in content.split("\\[url]".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()) {
            if (section.contains("[/url]")) {
                val urlSection =
                    section.split("\\[/url]".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                contentSections.add(Pair(urlSection[0], true))
                if (urlSection.size > 1) {
                    contentSections.add(
                        Pair(
                            urlSection[1],
                            false
                        )
                    )
                }
            } else {
                contentSections.add(Pair(section, false))
            }
        }
        return contentSections

    }
}
