package com.justwen.androidnga.module.message.compose.detail

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import com.justwen.androidnga.base.network.retrofit.RetrofitServiceKt
import com.justwen.androidnga.core.data.MessageArticlePageInfo
import com.justwen.androidnga.module.message.MessageConvertFactory
import kotlinx.coroutines.flow.Flow

object MessageDetailRepository {

    private const val PAGE_SIZE = 20

    var recipient: String? = null

    var msgTitle: String? = null

    fun getMessageDetailData(mid: String): Flow<PagingData<MessageArticlePageInfo>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { MessageDetailPagingSource(mid) }).flow
    }

}

class MessageDetailPagingSource(mid: String) : PagingSource<Int, MessageArticlePageInfo>() {

    private val netService =
        RetrofitHelper.getInstance().getService(RetrofitServiceKt::class.java) as RetrofitServiceKt

    /**
     * http://bbs.nga.cn/nuke.php?__lib=message&__act=message&act=read&page=1&mid=1&lite=js
     */
    private val paramMap: HashMap<String, String> =
        hashMapOf("__lib" to "message", "__act" to "message", "act" to "read", "lite" to "js")

    init {
        paramMap["mid"] = mid
    }

    override fun getRefreshKey(state: PagingState<Int, MessageArticlePageInfo>): Int? {
        return null;
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageArticlePageInfo> {
        try {
            val page = params.key ?: 1
            val preKey = if (page > 1) page - 1 else null
            paramMap["page"] = page.toString()
            val jsonString = netService.getString(paramMap)
            val factory = MessageConvertFactory()
            var nextKey: Int? = null
            val result = factory.getMessageDetailInfo(jsonString, page)?.let {
                nextKey = if (it.__nextPage > 0) page + 1 else null
                MessageDetailRepository.msgTitle = it._Title
                MessageDetailRepository.recipient = it._Alluser
                it.messageEntryList ?: emptyList()
            }

            if (!result.isNullOrEmpty()) {
                return LoadResult.Page(result, preKey, nextKey)
            } else {
                return LoadResult.Error(Exception(factory.errorMsg))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}