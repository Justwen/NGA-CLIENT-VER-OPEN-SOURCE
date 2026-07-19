package com.justwen.androidnga.module.message.compose.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justwen.androidnga.core.data.MessageArticlePageInfo
import kotlinx.coroutines.flow.Flow

class MessageDetailModel : ViewModel() {

    fun getMessageDetailData(mid: String): Flow<PagingData<MessageArticlePageInfo>> {
        return MessageDetailRepository.getMessageDetailData(mid).cachedIn(viewModelScope)
    }

    fun getRecipient(): String? {
        return MessageDetailRepository.recipient
    }

    fun getMessageTitle(): String? {
        return MessageDetailRepository.msgTitle
    }

}