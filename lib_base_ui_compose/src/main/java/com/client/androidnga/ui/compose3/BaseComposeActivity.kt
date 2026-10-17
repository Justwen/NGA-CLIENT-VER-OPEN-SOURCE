package com.client.androidnga.ui.compose3

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import com.client.androidnga.ui.compose3.widget.OptionMenuData

abstract class BaseComposeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            // 深色背景 → 浅色（白色）图标；transparent 让 TopAppBar 的 primary 透上来
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        buildContentView()
    }

    open fun buildContentView() {
//        setContent {
//            AppTheme {
//                ScaffoldApp(
//                    topAppBarData = getTopAppBarData(),
//                    fabClick = getFabClickAction(),
//                ) {
//                    Surface(
//                        modifier = Modifier.fillMaxSize(),
//                        color = MaterialTheme.colorScheme.background
//                    ) {
//                        ContentView()
//                    }
//                }
//            }
//        }
    }

//    open fun getTopAppBarData(): TopAppBarData {
//        val topAppBarData = TopAppBarData(title = title.toString())
//        topAppBarData.optionMenuData = getOptionMenuData()
//        topAppBarData.navigationIconAction = { finish() }
//        return topAppBarData
//    }

    open fun getOptionMenuData(): List<OptionMenuData> {
        return emptyList()
    }

    open fun getFabClickAction(): (() -> Unit)? {
        return null
    }

    @Composable
    abstract fun ContentView()
}