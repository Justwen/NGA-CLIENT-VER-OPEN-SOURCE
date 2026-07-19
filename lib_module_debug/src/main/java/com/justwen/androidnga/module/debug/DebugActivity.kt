package com.justwen.androidnga.module.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import gov.anzong.androidnga.base.util.ToastUtils

@Route(path = ARouterConstants.ACTIVITY_DEBUG)
class DebugActivity : BaseComposeActivity() {

    @Preview
    @Composable
    override fun ContentView() {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            var debugMode by remember {
                mutableStateOf(DebugManager.debugMode)
            }
            val modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(50.dp)
            Button(modifier = modifier, onClick = {
                debugMode = !debugMode
                DebugManager.debugMode = debugMode
                ToastUtils.show(if (DebugManager.debugMode) "调试模式开启" else "调试模式关闭")
            }) {
                val state = if (debugMode) "开" else "关"
                Text("调试模式：$state")
            }

            Button(modifier = modifier, onClick = {
                shareDebugFile()
            }) {
                Text("分享日志")
            }

            Button(modifier = modifier, onClick = {
                if (DebugManager.debugMode) {
                    ToastUtils.show("请先关闭调试模式")
                } else {
                    DebugManager.clearLogFiles()
                    ToastUtils.show("清除日志成功")
                }
            }) {
                Text("清除日志")
            }

        }
    }

    private fun shareDebugFile() {
        DebugManager.shareDebugFile(this)
    }
}