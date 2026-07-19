package com.justwen.androidnga.module.message.compose

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import com.justwen.androidnga.core.data.MessageThreadPageInfo
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import com.justwen.androidnga.module.message.MessageConvertFactory

object MessageRepository {

    private const val PAGE_SIZE = 20

    fun getMessageListData(): Flow<PagingData<MessageThreadPageInfo>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { MessagePagingSource() }).flow
    }

}

class MessagePagingSource : PagingSource<Int, MessageThreadPageInfo>() {

    private val paramMap: HashMap<String, String> =
        hashMapOf("__lib" to "message", "__act" to "message", "act" to "list", "lite" to "js")

    override fun getRefreshKey(state: PagingState<Int, MessageThreadPageInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageThreadPageInfo> {
        try {
            val page = params.key ?: 1
            val preKey = if (page > 1) page - 1 else null
            val netService =
                RetrofitHelper.getInstance().getService(RetrofitServiceKt::class.java) as RetrofitServiceKt
            paramMap["page"] = page.toString()
            val jsonString = netService.getString(paramMap)
            val factory = MessageConvertFactory()
            var nextKey: Int? = null
            val result = factory.getMessageListInfo(jsonString)?.let {
                nextKey = if (it.__nextPage > 0) page + 1 else null
                it.messageEntryList ?: emptyList()
            }

            if (!result.isNullOrEmpty()) {
                return LoadResult.Page(result, preKey, nextKey)
            } else {
                return LoadResult.Error(Exception())
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

}
