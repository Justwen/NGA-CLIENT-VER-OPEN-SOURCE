package com.justwen.androidnga.module.message.compose.post

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.module.message.R
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import gov.anzong.androidnga.base.util.ToastUtils

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_POST)
class MessagePostActivity : BaseComposeActivity() {

    private val viewModel: MessagePostModel by lazy {
        ViewModelProvider(this)[MessagePostModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initData(intent)
        if (!viewModel.isNewPostMode()) {
            setTitle(R.string.reply_message)
        } else {
            setTitle(R.string.new_message)
        }
        viewModel.observeResult().observe(this) { it ->
            it.onSuccess {
                ToastUtils.success(it)
                finish()
            }.onFailure {
                ToastUtils.error(it.message)
            }
        }
    }

    @Preview
    @Composable
    override fun ContentView() {
        Column(
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
            )
        ) {
            if (viewModel.isNewPostMode()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ) {
                    var recipient by remember { mutableStateOf(viewModel.postData.recipient) }
                    TextField(modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        value = recipient,
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                        onValueChange = {
                            recipient = it
                            viewModel.postData.recipient = it
                        },
                        placeholder = { Text(text = "输入ID/用户名") })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                var subject by remember { mutableStateOf("") }
                subject = viewModel.postData.postSubject
                TextField(modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    value = subject,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    onValueChange = {
                        subject = it
                        viewModel.postData.postSubject = it
                    },
                    placeholder = { Text(text = "标题") })
            }

            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                var content by remember { mutableStateOf("") }
                TextField(modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                    value = content,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    onValueChange = {
                        content = it
                        viewModel.postData.postContent = it
                    },
                    placeholder = { Text(text = "内容") })

                TextButton(modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    ),
                    onClick = {
                        viewModel.postMessage()
                    }) {
                    Text(text = "发送消息", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }

            }
        }

    }

}

