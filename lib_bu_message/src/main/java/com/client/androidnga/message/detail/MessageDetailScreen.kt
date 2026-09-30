package com.client.androidnga.message.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.justwen.androidnga.ui.compose.widget.PullRefreshColumn

@Composable
fun MessageDetailScreen(
    viewModel: MessageDetailViewModel,
    onUrlClick: (String) -> Unit = {}
) {
    val items = viewModel.messageDetailData.collectAsLazyPagingItems()

    PullRefreshColumn(
        columnItem = { MessageDetailItem(messageInfo = it, onUrlClick = onUrlClick) },
        lazyPagingItems = items,
        onRefresh = { items.refresh() })

}

private fun buildUrlString(
    contentSections: List<Pair<String, Boolean>>,
    onUrlClick: (String) -> Unit = {}
): AnnotatedString {
    return buildAnnotatedString {
        contentSections.forEach {
            val content = it.first
            val isUrl = it.second
            if (!isUrl) {
                withStyle(style = SpanStyle(fontSize = 16.sp)) {
                    append(content)
                }
            } else {
                withLink(
                    LinkAnnotation.Url(
                        url = content, styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    ) { link ->
                        onUrlClick((link as LinkAnnotation.Url).url)
                    }
                ) {
                    append(content)
                }
            }
        }
    }
}

@Preview
@Composable
fun MessageDetailItem(
    messageInfo: MessageDetailItem = MessageDetailItem(),
    onUrlClick: (String) -> Unit = {},
) {
    val annotatedText = buildUrlString(messageInfo.contentSections, onUrlClick)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp)
    ) {

        if (messageInfo.subject.isNotEmpty()) {
            Text(
                text = messageInfo.subject,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Text(annotatedText)

        var userName = messageInfo.author
        if (userName.isEmpty()) {
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