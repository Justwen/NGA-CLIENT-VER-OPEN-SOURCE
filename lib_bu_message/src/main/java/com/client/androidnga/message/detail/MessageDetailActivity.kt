package com.client.androidnga.message.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.ui.compose.BaseComposeActivity

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_DETAIL)
class MessageDetailActivity : BaseComposeActivity() {

    private val viewModel: MessageDetailViewModel by viewModels()

    override fun getFabClickAction(): () -> Unit {
        return {
            ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                .withString("mid", viewModel.mid)
                .withString("action", "reply")
                .withString("to", viewModel.recipient)
                .withString("title", viewModel.subject)
                .navigation(this)
        }
    }

    @Composable
    override fun ContentView() {
        MessageDetailScreen(viewModel) {
            startWebView(it)
        }
    }

    private fun startWebView(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = url.toUri()
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

}

