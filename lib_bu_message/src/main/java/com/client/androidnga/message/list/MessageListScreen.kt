package com.client.androidnga.message.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.client.androidnga.message.R
import com.justwen.androidnga.ui.compose.widget.PullRefreshColumn

@Composable
fun MessageListScreen(
    viewModel: MessageListViewModel,
    onMessageClick: (mid: String, subject: String, recipient: String) -> Unit,
) {
    val items = viewModel.messageListData.collectAsLazyPagingItems()
    MessageListContent(items, onMessageClick)
}

@Composable
private fun MessageListContent(
    items: LazyPagingItems<MessageListItem>,
    onMessageClick: (mid: String, subject: String, recipient: String) -> Unit,
) {
    PullRefreshColumn(
        columnItem = { MessageListRow(messageInfo = it, onMessageClick = onMessageClick) },
        lazyPagingItems = items, onRefresh = { items.refresh() }
    )
}

@Preview
@Composable
private fun MessageListRow(
    messageInfo: MessageListItem = MessageListItem(),
    onMessageClick: ((mid: String, subject: String, recipient: String) -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMessageClick?.invoke(
                    messageInfo.mid,
                    messageInfo.subject,
                    messageInfo.fromUsername
                )
            }
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.default_avatar),
            modifier = Modifier
                .padding(end = 16.dp)
                .size(36.dp),
            contentDescription = null
        )

        var userName = messageInfo.fromUsername
        if (userName.isEmpty()) {
            userName = "#SYSTEM#"
        }

        Column(modifier = Modifier.weight(2f)) {
            Text(text = userName, maxLines = 1, color = Color(0xFF545454), fontSize = 16.sp)
            Text(
                messageInfo.subject,
                Modifier.padding(top = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF294563),
                fontSize = 12.sp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = messageInfo.time.split(" ")[0],
                maxLines = 1,
                color = Color(0xFF294563),
                fontSize = 12.sp
            )
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    painter = painterResource(R.drawable.replies_icon),
                    contentDescription = null,
                )
                Text(
                    messageInfo.posts.toString(),
                    color = Color(0xFF294563),
                    fontSize = 12.sp
                )
            }

        }

    }
}