package com.client.androidnga.message.list

import gov.anzong.androidnga.common.base.JavaBean

class MessageListInfo {
    var rowsPerPage: Int = 0
    var nextPage: Int = 0
    var currentPage: Int = 0

    val messageItemList = mutableListOf<MessageListItem>()
}

class MessageListItem : JavaBean {
    var mid: String = ""

    var subject: String = ""

    var time: String = ""
    var lastTime: String = ""

    var posts: Int = 0

    var fromUsername: String = ""

    var lastFromUsername: String = ""
}