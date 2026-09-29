package com.justwen.androidnga.base.service.api

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import kotlinx.coroutines.flow.StateFlow

interface IUserManagerService : IProvider {

    companion object {
        const val ROUTER_PATH = "/user/service"
    }

    val activeAccountIndexState: StateFlow<Int>

    fun showUserSwitchDialog(context: Context, callback: (() -> Unit)? = null)

    fun addUser(uid: String, cid: String, name: String)

}