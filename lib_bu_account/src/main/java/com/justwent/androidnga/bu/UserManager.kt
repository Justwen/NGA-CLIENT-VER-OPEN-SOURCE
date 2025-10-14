package com.justwent.androidnga.bu

import androidx.lifecycle.MutableLiveData
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.utils.ThreadProvider
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.db.AppDatabase
import sp.phone.common.User

object UserManager {

    private val activeIndexLiveData: MutableLiveData<Int> = MutableLiveData(0)

    private var userListLiveData: MutableLiveData<List<User>> = MutableLiveData(mutableListOf())

    private var activeUser: User? = null

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
        return activeUser != null
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

    fun addUser(uid: String, cid: String, name: String) {
        val user = User(uid, name, cid)
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

}
