package com.client.androidnga.message.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

class MessageDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

//    private val repository = MessageListRepository()

    val mid: String = savedStateHandle["mid"] ?: ""

    val recipient: String = savedStateHandle["recipient"] ?: ""

    val subject: String = savedStateHandle["subject"] ?: ""

    val messageDetailData =
        MessageDetailRepository.getMessageDetailData(mid).cachedIn(viewModelScope)

}