package gov.anzong.androidnga.common.util

import androidx.core.net.toUri
import java.net.URLDecoder

fun String.getUrlValue(key: String): String? {
    return toUri().getQueryParameter(key)
}

fun String.getUrlIntValue(key: String): Int {
    val value = getUrlValue(key)
    return URLDecoder.decode(value, "utf-8").trim().toIntOrNull() ?: 0
}