package com.client.androidnga.core.parse

import com.alibaba.fastjson2.JSON
import com.client.androidnga.core.data.bean.ThreadAppBean
import com.client.androidnga.core.data.model.ThreadBasicInfo
import com.client.androidnga.core.data.model.ThreadInfo
import com.client.androidnga.core.data.model.ThreadPageInfo
import com.client.androidnga.core.data.model.ThreadPostInfo
import gov.anzong.androidnga.core.HtmlConvertFactory
import gov.anzong.androidnga.core.IHtmlConfigService

class ThreadInfoAppParse {

    companion object {
        @JvmStatic
        fun parse(rawData: String, config: IHtmlConfigService? = null): ThreadInfo? {
            return ThreadInfoAppParse().parseThreadInfo(rawData, config = config)
        }
    }

    private fun parseThreadInfo(
        jsStr: String,
        config: IHtmlConfigService?
    ): ThreadInfo? {
        val threadBean = JSON.parseObject(jsStr, ThreadAppBean::class.java) ?: return null
        val threadInfo = ThreadInfo().apply {
            rawData = jsStr
            basicInfo = parseBasicInfo(threadBean)
            totalRows = threadBean.vrows
            pageInfo = parsePageInfo(threadBean)
            postInfoList = parsePostInfoList(threadBean, basicInfo, config)

        }
        return threadInfo
    }

    private fun parsePostInfoList(
        threadBean: ThreadAppBean,
        basicInfo: ThreadBasicInfo?,
        iHtmlConfigService: IHtmlConfigService?,
    ): List<ThreadPostInfo> {
        val postInfoList = mutableListOf<ThreadPostInfo>()
        for (i in 0 until threadBean.result.size) {
            val result = threadBean.result[i]
            val postInfo = ThreadPostInfo().apply {
                pid = result.pid
                tid = result.tid
                fid = result.fid
                lou = result.lou
                alterInfo = result.alterinfo
                vote = result.vote
                postDate = result.postdate
                rawContent = result.content.ifEmpty {
                    result.subject
                }
                subject = result.subject
                if (i == 0 && subject.isNullOrEmpty() && threadBean.currentPage == 1) {
                    subject = threadBean.tsubject
                }
                clientModel = ThreadParseUtils.parseClientModel(result.from_client)
            }
            parseUserInfo(postInfo, result, iHtmlConfigService)
            postInfo.isThreadAuthor = postInfo.authorId == threadBean.tauthorid
            postInfo.formatHtml =
                HtmlConvertFactory.convert(postInfo, basicInfo, iHtmlConfigService)

            postInfoList.add(postInfo)
        }
        return postInfoList
    }

    fun parseUserInfo(
        postInfo: ThreadPostInfo,
        userBean: ThreadAppBean.Result,
        iHtmlConfigService: IHtmlConfigService?,
    ) {

        userBean.author?.let {
            postInfo.authorId = it.uid
            postInfo.postCount = it.postnum.toString()
            postInfo.isNuked = it.yz == -1
            postInfo.isMuted = it.buffs?.containsKey("105") ?: false
            postInfo.author = it.username
            postInfo.avatarUrl = it.avatar
            postInfo.isAnonymous = it.annoy?.startsWith("#anony_") == true
            postInfo.isBlocked =
                iHtmlConfigService?.isBlocked(postInfo.authorId.toString()) ?: false
            postInfo.signature = it.signature
            postInfo.memberGroup = it.member
            postInfo.reputation = it.rvrc?.toFloatOrNull()?.div(10.0f) ?: 0f
        }
    }

    private fun parsePageInfo(threadBean: ThreadAppBean): ThreadPageInfo {
        val pageInfo = ThreadPageInfo().apply {
            authorid = threadBean.tauthorid
            subject = threadBean.tsubject
            author = threadBean.tauthor
            fid = threadBean.fid
            tid = threadBean.result[0].tid
        }
        return pageInfo
    }

    private fun parseBasicInfo(threadBean: ThreadAppBean): ThreadBasicInfo {
        val basicInfo = ThreadBasicInfo().apply {
            attachHost =
                threadBean.attachPrefix?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()[0]
        }
        return basicInfo
    }
}

