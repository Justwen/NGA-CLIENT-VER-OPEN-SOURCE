package com.client.androidnga.message.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.androidnga.base.network.apiSafeCall
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import kotlinx.coroutines.flow.Flow

class MessageListRepository {

    fun getMessageListData(): Flow<PagingData<MessageListItem>> {
        return Pager(
            config = PagingConfig(pageSize = 35, initialLoadSize = 35, prefetchDistance = 10),
            pagingSourceFactory = { MessagePagingSource() }).flow
    }

}

class MessagePagingSource : PagingSource<Int, MessageListItem>() {

    private val paramMap =
        hashMapOf("__lib" to "message", "__act" to "message", "act" to "list", "__output" to "8")

    private val netService =
        RetrofitHelper.getInstance()
            .getService(RetrofitServiceKt::class.java) as RetrofitServiceKt

    override fun getRefreshKey(state: PagingState<Int, MessageListItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageListItem> {
        val page = params.key ?: 1
        val preKey = if (page > 1) page - 1 else null
        val requestParams = HashMap(paramMap).apply {
            this["page"] = page.toString()
        }
        val result = apiSafeCall(
            request = { netService.getString(requestParams) },
            convert = { MessageListParser().parse(it) })
        return result.data?.let {
            val nextKey = if (it.nextPage > 0) page + 1 else null
            LoadResult.Page(it.messageItemList, preKey, nextKey)
        } ?: return LoadResult.Error(result.error?.cause ?: Throwable())

    }

}
