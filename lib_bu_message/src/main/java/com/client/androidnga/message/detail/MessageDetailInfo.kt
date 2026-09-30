package com.client.androidnga.message.detail

import gov.anzong.androidnga.common.base.JavaBean

class MessageDetailInfo : JavaBean {
    @JvmField
    var nextPage: Int = 0

    @JvmField
    var currentPage: Int = 0

    @JvmField
    var title: String? = null

    @JvmField
    var messageEntryList: MutableList<MessageDetailItem> = mutableListOf()

}

class MessageDetailItem : JavaBean {
    @JvmField
    var subject: String = ""

    @JvmField
    var time: String = ""

    @JvmField
    var content: String = ""

    @JvmField
    var from: String? = null

    @JvmField
    var lou: Int = 0

    @JvmField
    var author: String = "" //user name

    @JvmField
    var contentSections: List<Pair<String, Boolean>> =
        emptyList()
}

