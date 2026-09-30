package com.client.androidnga.message.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import kotlinx.coroutines.CancellationException
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
        try {
            val page = params.key ?: 1
            val preKey = if (page > 1) page - 1 else null
            paramMap["page"] = page.toString()
            val jsonString = netService.getString(paramMap)
            val factory = MessageListParser()
            var nextKey: Int?
            val result = factory.parse(jsonString)
            result.getOrThrow().let {
                nextKey = if (it.nextPage > 0) page + 1 else null
                return LoadResult.Page(it.messageItemList, preKey, nextKey)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

}
