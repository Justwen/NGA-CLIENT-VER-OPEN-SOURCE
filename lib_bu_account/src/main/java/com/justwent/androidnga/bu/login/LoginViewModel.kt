package com.justwent.androidnga.bu.login

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import com.justwent.androidnga.bu.UserManager
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class LoginViewModel : ViewModel() {

    companion object {

        const val URL_LOGIN = "https://ngabbs.com/nuke.php?__lib=login&__act=account&login"

        const val LOGIN_SUCCESS_MSG = "登录成功 是否返回首页"

        const val TAG_UID: String = "ngaPassportUid"

        const val TAG_CID: String = "ngaPassportCid"

        const val TAG_USER_NAME: String = "ngaPassportUrlencodedUname"
    }

    private var loginResult: Boolean = false

    var currentUrl: String = URL_LOGIN

    fun checkLoginResult(result: Pair<String, String>): Boolean {
        if (result.first == URL_LOGIN && result.second.contains(LOGIN_SUCCESS_MSG)) {
            loginResult = checkLoginResult(result.first)
        }
        return loginResult
    }

    fun checkLoginResult(url: String = currentUrl): Boolean {
        if (loginResult) {
            return true
        }
        val cookieStr = CookieManager.getInstance().getCookie(url)
        cookieStr?.let {
            loginResult = parseCookie(it)
        }
        return loginResult
    }

    private fun parseCookie(cookies: String): Boolean {
        if (!cookies.contains(TAG_UID)) {
            return false
        }
        var uid: String? = null
        var cid: String? = null
        var userName: String? = null

        for (item in cookies.split(";".toRegex())) {
            val cookie = item.trim()
            if (cookie.contains(TAG_UID)) {
                uid = cookie.substring(TAG_UID.length + 1)
            } else if (cookie.contains(TAG_CID)) {
                cid = cookie.substring(TAG_CID.length + 1)
            } else if (cookie.contains(TAG_USER_NAME)) {
                userName = cookie.substring(TAG_USER_NAME.length + 1)
                try {
                    // 这里需要解析两遍，不是bug
                    userName = URLDecoder.decode(userName, "gbk")
                    userName = URLDecoder.decode(userName, "gbk")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }

        if (uid.isNullOrEmpty() || cid.isNullOrEmpty() || userName.isNullOrEmpty()) {
            return false
        }

        UserManager.addUser(uid, cid, userName)
        return true
    }

}