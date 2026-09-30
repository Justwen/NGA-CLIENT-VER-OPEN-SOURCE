package com.client.androidnga.message.post

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.client.androidnga.message.R
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import gov.anzong.androidnga.base.util.ToastUtils
import kotlinx.coroutines.launch

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_POST)
class MessagePostActivity : BaseComposeActivity() {

    private val viewModel: MessagePostViewModel by lazy {
        ViewModelProvider(this)[MessagePostViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.initialize(intent)
        if (!viewModel.isNewPostMode()) {
            setTitle(R.string.reply_message)
        } else {
            setTitle(R.string.new_message)
        }
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postEvent.collect { result ->
                    result.onSuccess {
                        ToastUtils.success(it)
                        finish()
                    }.onFailure {
                        ToastUtils.error(it.message ?: "发送失败")
                    }
                }
            }
        }
    }

    @Composable
    override fun ContentView() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        MessagePostScreen(uiState, viewModel)
    }
}

