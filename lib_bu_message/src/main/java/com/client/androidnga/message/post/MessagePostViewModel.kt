package com.client.androidnga.message.post

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gov.anzong.androidnga.common.base.JavaBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagePostViewModel : ViewModel() {

    private val repository: MessagePostRepository = MessagePostRepository()

    private val _uiState: MutableStateFlow<MessagePostUiState> =
        MutableStateFlow(MessagePostUiState())
    val uiState = _uiState.asStateFlow()

    private val _postEvent = Channel<Result<String>>(Channel.BUFFERED)
    val postEvent = _postEvent.receiveAsFlow()

    private var isInitialized = false

    companion object {
        private const val MIN_CONTENT_LENGTH = 6
    }


    fun initialize(intent: Intent) {
        if (isInitialized) {
            return
        }
        isInitialized = true
        _uiState.value = MessagePostUiState(
            mode = intent.getStringExtra("action") ?: "new",
            mid = intent.getStringExtra("mid") ?: "",
            recipient = intent.getStringExtra("to") ?: "",
            subject = intent.getStringExtra("title") ?: "",
        )
    }

    fun isNewPostMode(): Boolean {
        return uiState.value.isNewPostMode()
    }

    fun postMessage() {
        val state = uiState.value
        when {
            state.isSubmitting -> {
                _postEvent.trySend(
                    Result.failure(
                        Throwable("发送中，请勿重复点击")
                    )
                )
                return
            }

            isNewPostMode() && state.recipient.isEmpty() -> {
                _postEvent.trySend(
                    Result.failure(
                        Throwable("请输入收件人")
                    )
                )
                return
            }

            state.content.length < MIN_CONTENT_LENGTH -> {
                _postEvent.trySend(
                    Result.failure(
                        Throwable("请输入内容或者内容字数少于$MIN_CONTENT_LENGTH")
                    )
                )
                return
            }
        }

        _uiState.update {
            it.copy(isSubmitting = true)
        }

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.post(state)
                }
                _postEvent.send(result)
            } finally {
                // 网络异常、协程取消、服务端失败都能恢复按钮状态。
                _uiState.update {
                    it.copy(isSubmitting = false)
                }
            }
        }

    }

    fun updateRecipient(recipient: String) {
        _uiState.update {
            it.copy(recipient = recipient)
        }
    }

    fun updateSubject(subject: String) {
        _uiState.update {
            it.copy(subject = subject)
        }
    }

    fun updateContent(content: String) {
        _uiState.update {
            it.copy(content = content)
        }
    }

}

data class MessagePostUiState(
    val mode: String = "new",
    val mid: String = "",
    val subject: String = "",
    val content: String = "",
    val recipient: String = "",
    val isSubmitting: Boolean = false
) {
    fun isNewPostMode(): Boolean {
        return mode == "new"
    }
}