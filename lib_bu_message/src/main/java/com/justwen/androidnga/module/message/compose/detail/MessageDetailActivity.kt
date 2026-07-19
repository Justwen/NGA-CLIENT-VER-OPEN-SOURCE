package com.justwen.androidnga.module.message.compose.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.paging.compose.collectAsLazyPagingItems
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.core.data.MessageArticlePageInfo
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import com.justwen.androidnga.ui.compose.widget.PullRefreshColumn

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_DETAIL)
class MessageDetailActivity : BaseComposeActivity() {

    lateinit var mid: String

    private val viewModel: MessageDetailModel by lazy {
        ViewModelProvider(this)[MessageDetailModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mid = intent.getIntExtra("mid", 0).toString()
    }

    override fun getFabClickAction(): () -> Unit {
        return {
            ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                .withInt("mid", mid.toInt())
                .withString("action", "reply")
                .withString("to", viewModel.getRecipient())
                .withString("title", viewModel.getMessageTitle())
                .navigation(this)
        }
    }

    @Composable
    override fun ContentView() {
        val items = viewModel.getMessageDetailData(mid = mid).collectAsLazyPagingItems()

        PullRefreshColumn(columnItem = { MessageListItem(messageInfo = it) },
            lazyPagingItems = items,
            onRefresh = { items.refresh() })
    }

    private fun startWebView(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    @Preview
    @Composable
    fun MessageListItem(messageInfo: MessageArticlePageInfo = MessageArticlePageInfo()) {

        val annotatedText = buildAnnotatedString {
            messageInfo.contentSections.forEach(action = {
                if (!it.second) {
                    withStyle(style = SpanStyle(fontSize = 16.sp)) {
                        append(it.first)
                    }
                } else {
                    pushStringAnnotation(tag = "URL", annotation = it.first)
                    withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(it.first)
                    }
                    //代表结束
                    pop()
                }
            })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {

            if (!messageInfo.subject.isNullOrEmpty()) {
                Text(
                    text = messageInfo.subject,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            ClickableText(
                text = annotatedText,
                onClick = {
                    val annotationList =
                        annotatedText.getStringAnnotations(tag = "URL", start = it, end = it)
                    annotationList.firstOrNull()?.let { annotation ->
                        startWebView(annotation.item)
                    }
                },
            )

            var userName = messageInfo.author;
            if (TextUtils.isEmpty(userName)) {
                userName = "#SYSTEM#"
            }

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                    text = userName, fontSize = 14.sp, color = Color(0xFF294563),
                )
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterEnd),
                    text = messageInfo.time,
                    fontSize = 14.sp,
                    color = Color(0xFF294563)
                )
            }

        }
    }
}

