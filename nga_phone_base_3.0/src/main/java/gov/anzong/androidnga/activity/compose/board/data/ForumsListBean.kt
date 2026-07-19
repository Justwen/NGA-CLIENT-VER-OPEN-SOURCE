package gov.anzong.androidnga.activity.compose.board.data

import gov.anzong.androidnga.common.base.JavaBean

/**
 * 获取版块列表
 * Created by elrond on 2017/9/29.
 */
class ForumsListBean : JavaBean {
    var code: Int = 0
    var msg: String? = null
    var result: ArrayList<Result>? = null

    class Result : JavaBean {
        var id: String? = null
        var _id: String? = null
        var name: String? = null
        var groups: ArrayList<Group>? = null
    }

    class Group : JavaBean {
        var id: String? = null
        var name: String? = null
        var forums: ArrayList<Forum>? = null
    }

    class Forum : JavaBean {
        var id: Int = 0
        var name: String? = null

        var stid: Int = 0

    }
}
