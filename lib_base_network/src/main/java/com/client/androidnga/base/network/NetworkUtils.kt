package com.client.androidnga.base.network

import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <T : Any> apiSafeCall(
    crossinline request: suspend () -> String,
    convert: (str: String) -> ApiResult<T>,
): ApiResult<T> {
    return try {
        convert(request())
    } catch (e: CancellationException) {
        // 协程取消必须继续向上抛
        throw e
    } catch (e: Throwable) {
        ApiResult.failure(e = e)
    }
}