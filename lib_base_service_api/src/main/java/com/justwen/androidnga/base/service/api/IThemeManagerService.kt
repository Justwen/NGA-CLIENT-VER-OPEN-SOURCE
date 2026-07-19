package com.justwen.androidnga.base.service.api

import com.alibaba.android.arouter.facade.template.IProvider

interface IThemeManagerService : IProvider {

    companion object {
        const val ROUTER_PATH = "/service/theme"
    }

    fun isNightMode(): Boolean

    fun getThemeIndex(): Int

    fun getTheme(): Int

}