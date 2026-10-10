package com.client.androidnga.base.api.service

import com.alibaba.android.arouter.facade.template.IProvider

interface IFilterManagerService: IProvider {

    companion object {
        const val ROUTER_PATH = "/main/filter"
    }

    fun isFilterUser(uid: String): Boolean

    fun addFilterUser(uid: String, userName: String)

    fun removeFilterUser(uid: String)

}