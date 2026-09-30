package com.client.androidnga.message.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MessageInputField(
    modifier: Modifier = Modifier,
    value: String,
    hint: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier,
        singleLine = singleLine,
        value = value,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        onValueChange = {
            onValueChange(it)
        },
        placeholder = { Text(text = hint) })
}

@Preview
@Composable
fun MessagePostScreen(
    uiState: MessagePostUiState = MessagePostUiState(), viewModel: MessagePostViewModel? = null
) {
    Column(
        modifier = Modifier.padding(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
        )
    ) {
        if (uiState.isNewPostMode()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                MessageInputField(
                    value = uiState.recipient,
                    hint = "输入ID/用户名",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel?.updateRecipient(it)
                }
            }
        }
        MessageInputField(
            value = uiState.subject,
            hint = "标题",
            modifier = Modifier.fillMaxWidth()
        ) {
            viewModel?.updateSubject(it)
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            MessageInputField(
                value = uiState.content,
                hint = "内容",
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                viewModel?.updateContent(it)
            }

            TextButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.primary, contentColor = Color.White
                ),
                onClick = {
                    viewModel?.postMessage()
                }) {
                Text(text = "发送消息", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }

        }
    }

}