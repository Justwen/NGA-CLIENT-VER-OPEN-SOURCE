package gov.anzong.androidnga.activity.compose

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.android.arouter.launcher.ARouter
import gov.anzong.androidnga.arouter.ARouterConstants
import gov.anzong.androidnga.base.util.ToastUtils
import gov.anzong.androidnga.core.board.data.BoardEntity
import sp.phone.common.UserManagerImpl
import sp.phone.mvp.model.entity.Board
import sp.phone.param.ParamKey
import sp.phone.task.SearchBoardTask
import sp.phone.util.ActivityUtils

class SearchViewModel : ViewModel() {

    companion object {
        const val SEARCH_MODE_USER: Int = 0
        const val SEARCH_MODE_TOPIC: Int = 1
        const val SEARCH_MODE_BOARD: Int = 2

        const val SEARCH_MODE_USER_NAME: String = "username"
        const val SEARCH_MODE_USER_ID: String = "uid"

        const val SEARCH_MODE_TOPIC_CURRENT: String = "current"
        const val SEARCH_MODE_TOPIC_ALL: String = "all"

    }

    val searchData: List<Pair<String, Int>> = listOf(
        Pair("搜主题", SEARCH_MODE_TOPIC),
        Pair("搜板块", SEARCH_MODE_BOARD),
        Pair("搜用户", SEARCH_MODE_USER),
    )

    val searchUserData: List<Pair<String, String>> = listOf(
        Pair("用户名", SEARCH_MODE_USER_NAME),
        Pair("用户ID", SEARCH_MODE_USER_ID),
    )

    val searchTopicData: List<Pair<String, String>> = listOf(
        Pair("当前板块", SEARCH_MODE_TOPIC_CURRENT),
        Pair("全部板块", SEARCH_MODE_TOPIC_ALL),
    )

    var searchMode: MutableLiveData<Int> = object : MutableLiveData<Int>(SEARCH_MODE_TOPIC) {

        override fun setValue(value: Int?) {
            if (getValue() == value) return
            keyList.value = searchModel.getSearchHistory(value!!)
            super.setValue(value)
        }

        override fun postValue(value: Int?) {
            if (getValue() == value) return
            keyList.value = searchModel.getSearchHistory(value!!)
            super.postValue(value)
        }
    }

    var searchUserMode: String = SEARCH_MODE_USER_NAME

    var searchTopicMode: String = SEARCH_MODE_TOPIC_CURRENT

    var searchTopicWithContent: Boolean = false

    var fid: Int = 0

    var keyList = MutableLiveData<List<String>>()

    private val searchModel: SearchModel = SearchModel()

    init {
        keyList.value = searchModel.getSearchHistory(searchMode.value!!)
    }

    fun getSearchTintText(searchMode: Int): String {
        return when (searchMode) {
            SEARCH_MODE_USER -> "默认查看自己的用户信息"
            SEARCH_MODE_BOARD -> "强撸灰飞烟灭"
            SEARCH_MODE_TOPIC -> "强撸灰飞烟灭"
            else -> "搜索"
        }
    }

    fun query(context: Context, query: String) {
        when (searchMode.value) {
            SEARCH_MODE_USER -> queryUser(query)
            SEARCH_MODE_TOPIC -> queryTopic(query)
            SEARCH_MODE_BOARD -> queryBoard(context, query)
        }
    }

    private fun queryBoard(context: Context, query: String) {
        if (query.isEmpty()) {
            return
        }
        putHistory(searchMode.value!!, query)

        ActivityUtils.getInstance().noticeSaying(context)

        SearchBoardTask.execute(query) { data: BoardEntity? ->
            ActivityUtils.getInstance().dismiss()
            if (data == null) {
                ToastUtils.info("没有找到符合条件的版面或者网络错误")
            } else {
                ARouter.getInstance()
                    .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                    .withInt(ParamKey.KEY_FID, data.fid)
                    .withString(ParamKey.BOARD_HEAD, data.head)
                    .withString(ParamKey.KEY_TITLE, data.name)
                    .navigation()
            }
        }
    }

    private fun queryUser(query: String) {
        var realQuery = query
        if (query.isEmpty()) {
            val user = UserManagerImpl.getInstance().activeUser ?: return
            realQuery = if (searchUserMode == SEARCH_MODE_USER_NAME) user.nickName else user.userId
        } else {
            putHistory(searchMode.value!!, query)
        }
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_PROFILE)
            .withString("mode", searchUserMode)
            .withString(searchUserMode, realQuery)
            .navigation()
    }

    private fun queryTopic(query: String) {
        if (query.isEmpty()) {
            return
        }

        putHistory(searchMode.value!!, query)

        val postcard = ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
            .withInt("content", if (searchTopicWithContent) 1 else 0)
            .withString("key", query)
        if (searchTopicMode == SEARCH_MODE_TOPIC_CURRENT && fid != 0) {
            postcard.withInt("fid", fid)
        }
        postcard.navigation()
    }

    private fun putHistory(searchMode: Int, key: String) {
        val newKeyList = searchModel.putHistory(searchMode, key)
        newKeyList?.let {
            keyList.value = it
        }
    }

    fun deleteHistory(key: String) {
        val newKeyList = searchModel.deleteHistory(searchMode.value!!, key)
        newKeyList?.let {
            keyList.value = it
        }
    }

}
