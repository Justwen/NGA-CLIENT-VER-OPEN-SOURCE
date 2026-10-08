package com.client.androidnga.base.network

import com.alibaba.fastjson2.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException


data class ApiResult<out T>(
    val data: T? = null,
    val error: ApiError? = null
) {

    fun isSuccess(): Boolean {
        return data != null
    }

    fun isFailure(): Boolean {
        return error != null
    }

    companion object {

        fun <T> success(data: T): ApiResult<T> {
            return ApiResult(data = data)
        }

        fun failure(
            e: Throwable,
            message: String? = null,
        ): ApiResult<Nothing> {
            return ApiResult(error = buildError(e, message))
        }

        private fun buildError(
            e: Throwable,
            message: String? = null,
        ): ApiError {
            return when (e) {
                is BusinessException -> ApiError.BusinessError(
                    code = e.code,
                    message = e.message,
                    cause = e
                )

                is SocketTimeoutException -> ApiError.TimeoutError(e)
                is JSONException -> ApiError.JsonParseError(message, e)
                is HttpException -> ApiError.HttpException(
                    code = e.code(),
                    message = e.message(),
                    errorBody = e.response()
                        ?.errorBody()
                        ?.string(),
                    cause = e
                )

                is IOException -> ApiError.NetworkUnavailable(e)
                is CancellationException -> throw e
                else -> ApiError.UnexpectedError(e)

            }
        }
    }
}

data class BusinessException(val code: String? = null, override val message: String) : Exception()

sealed interface ApiError {

    var cause: Throwable

    /**
     * 无网络、DNS 解析失败。
     */
    data class NetworkUnavailable(override var cause: Throwable) : ApiError

    /**
     * HTTP 状态码异常，例如 401、404、500。
     */
    data class HttpException(
        val code: Int,
        val message: String?,
        val errorBody: String? = null,
        override var cause: Throwable
    ) : ApiError

    /**
     * 请求超时。
     */
    data class TimeoutError(override var cause: Throwable) : ApiError

    /**
     * JSON 序列化或反序列化失败。
     */
    data class JsonParseError(val jsonStr: String?, override var cause: Throwable) :
        ApiError

    /**
     * 服务端返回成功的 HTTP 响应，
     * 但业务状态码表示失败。
     */
    data class BusinessError(
        val code: String? = null, val message: String, override var cause: Throwable
    ) : ApiError

    /**
     * 未预期异常。
     */
    data class UnexpectedError(override var cause: Throwable) : ApiError

}