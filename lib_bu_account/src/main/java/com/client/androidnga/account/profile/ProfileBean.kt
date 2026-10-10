package com.client.androidnga.account.profile

import gov.anzong.androidnga.common.base.JavaBean

data class ProfileBean(
    var data: Map<String, ProfileData> = emptyMap(),
    var error: Map<String, String> = emptyMap(),
    var time: Int = 0
) : JavaBean

data class ProfileData(
    var _admin: Int = 0,
    var _greater: Int = 0,
    var _lesser: Int = 0,
    var _super: Int = 0,
    var adfree: Int = 0,
    var avatar: String = "",
    var bit: Int = 0,
    var clientChat: Int = 0,
    var email: String = "",
    var faction: Int = 0,
    var fame: Int = 0,
    var gid: Int = 0,
    var group: String = "",
    var groupid: Int = 0,
    var honor: String = "",
    var ipLoc: String = "",
    var items: Int = 0,
    var lastpost: Int = 0,
    var lastvisit: Int = 0,
    var medal: String = "",
    var memberid: Int = 0,
    var money: Int = 0,
    var muteTime: Int = 0,
    var newpm: Int = 0,
    var phone: String = "",
    var posts: Int = 0,
    var regdate: Int = 0,
    var remark: String = "",
    var reputation: Map<String, Map<String, Any>> = emptyMap(),
    var rvrc: Int = 0,
    var sign: String = "",
    var thisvisit: Int = 0,
    var title: String = "",
    var uid: Int = 0,
    var umengChat: Int = 0,
    var userForum: String = "",
    var username: String = "",
    var verified: Int = 0,
    var yz: Int = 0,
    var adminForums: Map<String, String> = emptyMap()
) : JavaBean