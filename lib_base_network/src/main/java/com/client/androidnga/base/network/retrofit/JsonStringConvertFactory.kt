package com.client.androidnga.base.network.retrofit

import gov.anzong.androidnga.base.logger.Logger
import okhttp3.ResponseBody
import org.apache.commons.io.IOUtils
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by Justwen on 2017/10/10.
 */
object JsonStringConvertFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type === String::class.java) {
            return JsonStringConverter
        }
        return super.responseBodyConverter(type, annotations, retrofit)
    }

    private object JsonStringConverter : Converter<ResponseBody, String> {

        override fun convert(responseBody: ResponseBody): String? {
            try {
                responseBody.byteStream().use { it ->
                    val body = IOUtils.toString(it, "GBK")
                    Logger.d(body)
                    return body
                }
            } catch (e: IOException) {
                return ""
            }
        }
    }

}
