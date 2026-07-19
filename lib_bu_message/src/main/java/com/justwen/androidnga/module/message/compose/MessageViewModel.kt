package com.justwen.androidnga.module.message.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justwen.androidnga.core.data.MessageThreadPageInfo
import kotlinx.coroutines.flow.Flow

class MessageViewModel : ViewModel() {

    fun getMessageListData(): Flow<PagingData<MessageThreadPageInfo>> {
        return MessageRepository.getMessageListData().cachedIn(viewModelScope)
    }
}