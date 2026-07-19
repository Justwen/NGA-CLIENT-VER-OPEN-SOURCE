package com.justwen.androidnga.module.message.compose.post

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagePostModel : ViewModel() {

    lateinit var postData: MessagePostData

    private val resultLiveData: MutableLiveData<Result<String>> = MutableLiveData()

    private var isRunning: Boolean = false

    fun initData(intent: Intent) {
        val mid = intent.getIntExtra("mid", 0).toString()
        val action = intent.getStringExtra("action") ?: "new"
        postData = MessagePostData(action, mid)
        postData.recipient = intent.getStringExtra("to") ?: ""
        postData.postSubject = intent.getStringExtra("title") ?: ""
    }

    fun observeResult(): MutableLiveData<Result<String>> {
        return resultLiveData
    }

    fun isNewPostMode(): Boolean {
        return postData.action == "new"
    }

    fun postMessage() {
        if (isNewPostMode() && postData.recipient.isEmpty()) {
            resultLiveData.value = Result.failure(Throwable("请输入收件人"))
            return
        } else if (postData.postContent.length <= 5) {
            resultLiveData.value = Result.failure(Throwable("请输入内容或者内容字数少于6"))
            return
        }

        if (isRunning) {
            resultLiveData.value = Result.failure(Throwable("发送中，请勿重复点击"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isRunning = true
            resultLiveData.postValue(MessagePostRepository.post(postData))
            isRunning = false
        }

    }
}

data class MessagePostData(val action: String, val mid: String) {

    var postSubject = ""

    var postContent: String = ""

    var recipient: String = ""
}