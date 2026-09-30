package com.client.androidnga.message.detail

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

object MessageDetailRepository {

    private const val PAGE_SIZE = 20

    fun getMessageDetailData(mid: String): Flow<PagingData<MessageDetailItem>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { MessageDetailPagingSource(mid) }).flow
    }

}

class MessageDetailPagingSource(mid: String) : PagingSource<Int, MessageDetailItem>() {

    private val netService =
        RetrofitHelper.getInstance().getService(RetrofitServiceKt::class.java) as RetrofitServiceKt

    /**
     * http://bbs.nga.cn/nuke.php?__lib=message&__act=message&act=read&page=1&mid=1&lite=js
     */
    private val defaultParam =
        mapOf(
            "__lib" to "message",
            "__act" to "message",
            "act" to "read",
            "lite" to "js",
            "mid" to mid
        )

    override fun getRefreshKey(state: PagingState<Int, MessageDetailItem>): Int? {
        return null;
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDetailItem> {
        try {
            val page = params.key ?: 1
            val preKey = if (page > 1) page - 1 else null
            val requestParams = HashMap(defaultParam).apply {
                this["page"] = page.toString()
            }
            val jsonString = netService.getString(requestParams)
            val factory = MessageDetailParser()
            var result: LoadResult<Int, MessageDetailItem>? = null
            factory.parse(jsonString).onFailure {
                result = LoadResult.Error(Exception(it.message))
            }.onSuccess {
                val nextKey = if (it.nextPage > 0) it.nextPage else null
                result = LoadResult.Page(it.messageEntryList, preKey, nextKey)
            }
            // 确保一定有返回值
            return result ?: LoadResult.Error(Exception("Unknown parsing error"))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}