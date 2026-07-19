package com.justwen.androidnga.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.justwen.androidnga.ui.compose.theme.AppTheme
import com.justwen.androidnga.ui.compose.widget.OptionMenuData
import com.justwen.androidnga.ui.compose.widget.ScaffoldApp
import com.justwen.androidnga.ui.compose.widget.TopAppBarData

abstract class BaseComposeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        buildContentView()
    }

    fun buildContentView() {
        setContent {
            AppTheme {
                ScaffoldApp(
                    topAppBarData = getTopAppBarData(),
                    fabClick = getFabClickAction(),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        ContentView()
                    }
                }
            }
        }
    }

    open fun getTopAppBarData(): TopAppBarData {
        val topAppBarData = TopAppBarData(title = title.toString())
        topAppBarData.optionMenuData = getOptionMenuData()
        topAppBarData.navigationIconAction = { finish() }
        return topAppBarData
    }

    open fun getOptionMenuData(): List<OptionMenuData>? {
        return null
    }

    open fun getFabClickAction(): (() -> Unit)? {
        return null
    }

    @Composable
    abstract fun ContentView()
}