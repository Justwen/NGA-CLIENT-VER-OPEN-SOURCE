package com.justwent.androidnga.bu

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.service.api.IUserManagerService
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.util.ToastUtils
import gov.anzong.androidnga.base.utils.ThreadProvider
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.db.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import sp.phone.common.User

object UserManager : IUserManagerService {

    private val activeIndexLiveData: MutableLiveData<Int> = MutableLiveData(0)

    private var userListLiveData: MutableLiveData<List<User>> = MutableLiveData(mutableListOf())

    private var activeUser: User? = null

    private val _accountState = MutableStateFlow(0)

    override val activeAccountIndexState: StateFlow<Int>
        get() = _accountState.asStateFlow()

    init {
        var index = PreferenceUtils.getData(PreferenceKey.USER_ACTIVE_INDEX, 0)
        AppDatabase.getInstance().userDao().loadUser()?.let {
            userListLiveData.postValue(it)
            if (it.isNotEmpty()) {
                if (index >= it.size || index < 0) {
                    index = 0
                }
                activeIndexLiveData.postValue(index)
                activeUser = it[index]
                _accountState.value = index
            }
        }
    }

    private fun saveUsers() {
        ThreadProvider.runOnSingleThread {
            synchronized(this) {
                AppDatabase.getInstance().userDao()
                    .updateUsers(*userListLiveData.value!!.toTypedArray<User>())
            }
        }
    }

    fun hasValidUser(): Boolean {
        return activeUser != null && userListLiveData.value?.isNotEmpty() == true
    }

    public fun getActiveIndex(): Int {
        return activeIndexLiveData.value!!
    }

    public fun getActiveIndexLiveData(): MutableLiveData<Int> {
        return activeIndexLiveData
    }

    fun getActiveUser(): User? {
        return activeUser
    }

    public fun setActiveIndex(index: Int) {
        activeIndexLiveData.value = index
        activeUser = userListLiveData.value!![index]
        _accountState.value = index
        PreferenceUtils.putData(PreferenceKey.USER_ACTIVE_INDEX, index)
    }

    fun toggleUser(isNext: Boolean): Int {
        val activeUserIndex = getNextActiveIndex(isNext)
        setActiveIndex(activeUserIndex)
        return activeUserIndex
    }

    public fun getNextActiveIndex(isNext: Boolean): Int {
        val size = userListLiveData.value!!.size
        if (size == 0) {
            return -1
        }
        val activeIndex = activeIndexLiveData.value!!

        val index = if (isNext) activeIndex + 1 else activeIndex + size - 1
        return index % size
    }

    fun addUser(user: User) {
        val userList = userListLiveData.value!!.toMutableList()
        var index: Int? = null
        for (i in userList.indices) {
            if (userList[i].userId == user.userId) {
                index = i
                break
            }
        }
        if (index == null) {
            userList.add(user)
        } else {
            userList[index] = user
        }
        activeUser = userList[activeIndexLiveData.value!!]
        userListLiveData.value = userList
        saveUsers()
    }

    override fun showUserSwitchDialog(
        context: Context,
        callback: (() -> Unit)?
    ) {
        val users = getUserList()
        if (users.isEmpty()) {
            return
        }
        val index = getActiveIndex()
        val items = Array<CharSequence>(users.size) { i -> users[i].nickName }

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setSingleChoiceItems(
            items, index
        ) { dialog, which ->
            run {
                setActiveIndex(which)
                ToastUtils.info(
                    "切换账户成功,当前账户名:" + items[index]
                )
                dialog.dismiss()
                callback?.invoke()
            }
        }.setTitle("切换账号")
        dialogBuilder.show()
    }

    override fun addUser(uid: String, cid: String, name: String) {
        val user = User()
        user.setCid(cid)
        user.setUserId(uid)
        user.setNickName(name)
        addUser(user)
    }

    fun removeUser(index: Int) {
        val userList = userListLiveData.value!!.toMutableList()
        val user = userList.removeAt(index)
        var activeIndex = activeIndexLiveData.value!!
        if (activeIndex >= index) {
            activeIndex -= 1
        }

        if (activeIndex >= userList.size || activeIndex < 0) {
            activeIndex = 0
        }
        if (userList.isEmpty()) {
            activeUser = null
            activeIndex = 0
            activeIndexLiveData.value = 0
            _accountState.value = 0
            PreferenceUtils.putData(PreferenceKey.USER_ACTIVE_INDEX, activeIndex)
        } else {
            setActiveIndex(activeIndex)
        }
        userListLiveData.value = userList
        ThreadProvider.runOnSingleThread {
            AppDatabase.getInstance().userDao().removeUsers(user)
        }
    }

    fun getUserListLiveData(): MutableLiveData<List<User>> {
        return userListLiveData
    }

    fun getUserList(): List<User> {
        return userListLiveData.value!!
    }

    fun getCookie(user: User? = activeUser): String {
        user?.let {
            if (it.cid.isNotEmpty() && it.userId.isNotEmpty()) {
                return "ngaPassportUid=" + it.userId + "; ngaPassportCid=" + it.cid
            }
        }
        return ""
    }

    fun getNextCookie(): String {
        val next = getNextActiveIndex(true)
        return getCookie(userListLiveData.value!![next])
    }

    fun setAvatarUrl(uid: String, url: String) {
        userListLiveData.value?.forEach {
            if (it.userId == uid) {
                if (it.avatarUrl != url) {
                    it.avatarUrl = url
                    ThreadProvider.runOnSingleThread {
                        AppDatabase.getInstance().userDao().updateUsers(it)
                    }
                }
                return
            }
        }
    }

    override fun init(context: Context?) = Unit

}

@Route(path = IUserManagerService.ROUTER_PATH)
class UserManagerService : IUserManagerService {

    override val activeAccountIndexState: StateFlow<Int>
        get() = UserManager.activeAccountIndexState

    override fun showUserSwitchDialog(context: Context, callback: (() -> Unit)?) {
        UserManager.showUserSwitchDialog(context, callback)
    }

    override fun addUser(uid: String, cid: String, name: String) {
        UserManager.addUser(uid, cid, name)
    }

    override fun init(context: Context?) = Unit
}
