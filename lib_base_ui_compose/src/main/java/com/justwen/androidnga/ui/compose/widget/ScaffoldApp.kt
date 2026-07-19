package com.justwen.androidnga.ui.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun FloatingActionButton(fabClick: (() -> Unit)? = null) {
    if (fabClick != null) {
        FloatingActionButton(
            modifier = Modifier.navigationBarsPadding(),
            backgroundColor = MaterialTheme.colors.primary,
            onClick = { fabClick.invoke() }) {
            Icon(Icons.Default.Add, tint = Color.White, contentDescription = "Add")
        }
    }
}

@Composable
fun OptionActionMenu(optionActions: List<OptionMenuData>? = null) {
    if (optionActions == null) {
        return
    }

    val showItems = optionActions.filter { it.type == OptionMenuData.OPTION_MENU_TYPE_ALWAYS_SHOW }
    val hideItems = optionActions.filter { it.type == OptionMenuData.OPTION_MENU_TYPE_HIDDEN }

    Row(verticalAlignment = Alignment.CenterVertically) {
        showItems.forEach {
            IconButton(onClick = {
                it.action()
            }) {
                Icon(
                    painter = painterResource(it.icon!!),
                    contentDescription = "",
                    tint = Color.White
                )
            }

        }
        var expanded by remember { mutableStateOf(false) }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "", tint = Color.White)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                hideItems.forEach(action = {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        it.action()
                    }) {
                        Text(text = it.title!!)
                    }
                })
            }
        }

    }

}

@Composable
fun TopAppBarEx(
    topAppBarData: TopAppBarData,
) {
    val paddingValues = WindowInsets.statusBars.asPaddingValues()
    val top = paddingValues.calculateTopPadding()
    val pxValue = with(LocalDensity.current) { top.toPx() }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        windowInsets = WindowInsets(0, pxValue.toInt(), 0, 0),
        title = {
            if (topAppBarData.customTopBar == null) {
                Text(text = topAppBarData.title, color = Color.White)
            } else {
                topAppBarData.customTopBar!!.invoke()
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                topAppBarData.navigationIconAction?.invoke()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            OptionActionMenu(optionActions = topAppBarData.optionMenuData)
        })
}

data class TopAppBarData(val title: String) {
    var navigationIconAction: (() -> Unit)? = null
    var optionMenuData: List<OptionMenuData>? = null
    var customTopBar: @Composable (() -> Unit)? = null
}

data class OptionMenuData(
    val title: String? = null,
    val icon: Int? = null,
    val action: (() -> Unit),
    val type: Int = OPTION_MENU_TYPE_HIDDEN
) {
    companion object {
        const val OPTION_MENU_TYPE_HIDDEN = 1
        const val OPTION_MENU_TYPE_ALWAYS_SHOW = 2
    }
}

@Preview()
@Composable
fun ScaffoldApp(
    topAppBarData: TopAppBarData = TopAppBarData("App"),
    fabClick: (() -> Unit)? = null,
    appContent: @Composable (() -> Unit)? = null,
) {
    rememberSystemUiController().run {
        setStatusBarColor(MaterialTheme.colors.primary, false)
    }
    Scaffold(
        topBar = {
            TopAppBarEx(
                topAppBarData = topAppBarData
            )
        },
        floatingActionButton = {
            FloatingActionButton(fabClick = fabClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            appContent?.invoke()
        }
    }
}