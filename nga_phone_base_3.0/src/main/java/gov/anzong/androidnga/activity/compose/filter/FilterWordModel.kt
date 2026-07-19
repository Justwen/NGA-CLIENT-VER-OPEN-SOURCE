package gov.anzong.androidnga.activity.compose.filter

import com.alibaba.fastjson.JSON
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import com.justwent.androidnga.bu.UserManager
import gov.anzong.androidnga.Utils
import gov.anzong.androidnga.base.logger.Logger
import gov.anzong.androidnga.common.base.JavaBean
import java.net.URLEncoder


class FilterWordModel {

    class FilterWordBean : JavaBean {
        var data: HashMap<String, String>? = null
        var error: HashMap<String, String>? = null
        var time: Int = 0
    }

    companion object {

        fun convertEntity(jsonString: String): Result<Pair<List<String>, List<String>>> {
            Logger.d(jsonString)
            try {
                val bean = JSON.parseObject(jsonString, FilterWordBean::class.java)
                bean.data?.let {
                    val data = it["0"]!!.split("\n")
                    if (data.size <= 2) {
                        return Result.success(Pair(emptyList(), emptyList()))
                    }
                    val filterWordList = data[1].split(" ").toList()
                    val filterUserList = buildList {
                        data[2].split(" ").forEach { item ->
                            if (item.isNotEmpty()) {
                                add(item)
                            }
                        }
                    }
                    return Result.success(Pair(filterUserList, filterWordList))
                }
                bean.error?.let {
                    val error = it["0"]
                    return Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }
            return Result.failure(Exception("未知错误"))
        }
    }

    suspend fun updateRemoteFilterList(
        filterUserList: List<String>,
        filterWordList: List<String>
    ): Result<String?> {
        UserManager.getActiveUser()?.let {
            val service =
                RetrofitHelper.getInstance().createRetrofit().create(RetrofitServiceKt::class.java)
            val params = HashMap<String, String>()
            params["__lib"] = "ucp"
            params["__act"] = "set_block_word"
            params["__output"] = "8"
            params["data"] = URLEncoder.encode(buildPostData(filterUserList, filterWordList), "gbk")
            Logger.d("data: ${params["data"]}")
            val refererStr = Utils.getNGAHost()
            val headerMap = mapOf(
                "Referer" to refererStr,
                "charset" to "GBK",
                "Host" to "bbs.nga.cn",
                "Origin" to refererStr,
                "content-type" to "application/x-www-form-urlencoded",
                "content-length" to "71"
            )
            val result =
                service.postString(headerMap = headerMap, fieldMap = params)
            return convertUpdateResult(result)
        }
        return Result.failure(Exception("未知错误"))
    }


    private fun convertUpdateResult(jsonString: String): Result<String?> {
        try {
            val bean = JSON.parseObject(jsonString, FilterWordBean::class.java)
            bean.data?.let {
                val data = it["0"]
                return Result.success(data)
            }
            bean.error?.let {
                val error = it["0"]
                return Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.failure(Exception("未知错误"))
    }

    private fun buildPostData(filterUserList: List<String>, filterWordList: List<String>): String {
        val data = StringBuilder()
        data.append("1\r\n").append(filterWordList.joinToString(" ")).append("\r\n")
            .append(filterUserList.joinToString(" "))
        return data.toString()
    }

    suspend fun requestRemoteFilterList(): Result<Pair<List<String>, List<String>>> {
        UserManager.getActiveUser()?.let {
            val service =
                RetrofitHelper.getInstance().createRetrofit()
                    .create(RetrofitServiceKt::class.java)
            val params = HashMap<String, String>()
            params["__lib"] = "ucp"
            params["__act"] = "get_block_word"
            params["__output"] = "8"
            params["uid"] = it.userId
            val refererStr = "${Utils.getNGAHost()}nuke.php?func=ucp&uid=${it.userId}"
            val headerMap = mapOf("Referer" to refererStr)
            return convertEntity(service.postString(headerMap = headerMap, fieldMap = params))
        }
        return Result.failure(Exception("用户信息错误，请尝试重新登录"))
    }

}