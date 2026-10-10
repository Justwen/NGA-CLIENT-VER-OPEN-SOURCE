package com.client.androidnga.account.profile

import com.alibaba.fastjson2.JSON
import com.client.androidnga.base.network.ApiError
import com.client.androidnga.base.network.ApiResult
import com.client.androidnga.base.network.apiSafeCall
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import java.net.URLEncoder

class ProfileRepository {

    private val _paramMap =
        mapOf("func" to "ucp", "lite" to "js", "__lib" to "ucp", "__act" to "get")


    suspend fun loadProfile(uid: String = "", userName: String = ""): ApiResult<ProfileData> {
        val paramMap = HashMap(_paramMap).apply {
            this["uid"] = uid
            this["username"] = URLEncoder.encode(userName, "gbk")
        }
        val service = RetrofitHelper.getInstance().serviceKt
        return apiSafeCall(
            request = { service.getStringEncoded(paramMap) },
            convert = { parse(it) })
    }

    fun parse(js: String): ApiResult<ProfileData> {
        val bean = JSON.parseObject(
            js.removePrefix("window.script_muti_get_var_store="),
            ProfileBean::class.java
        )
        bean.data["0"]?.let {
            return ApiResult.success(it)
        }
        if (bean.error.isNotEmpty()) {
            bean.error["0"]?.let {
                return ApiResult(
                    error = ApiError.BusinessError(
                        message = it,
                        cause = Exception(it)
                    )
                )
            }
        }
        return ApiResult(error = ApiError.UnexpectedError(Exception("出现未知错误")))
    }


}
