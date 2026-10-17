package com.client.androidnga.ui.compose3.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

@Composable
fun OptionActionMenu(optionActions: List<OptionMenuData> = emptyList()) {
    val (showItems, hideItems) = optionActions.partition { it.type == OptionMenuData.OPTION_MENU_TYPE_ALWAYS_SHOW }
    Row(verticalAlignment = Alignment.CenterVertically) {
        showItems.forEach {
            IconButton(onClick = {
                it.action()
            }) {
                Icon(
                    painter = painterResource(it.icon),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
        if (hideItems.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = Color.White
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    hideItems.forEach(action = {
                        DropdownMenuItem(
                            text = { Text(it.title) },
                            onClick = {
                                expanded = false
                                it.action()
                            },
                        )
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarEx(
    title: String,
    optionMenuData: List<OptionMenuData> = emptyList(),
    navigationIconAction: (() -> Unit)? = null,
) {
    val paddingValues = WindowInsets.statusBars.asPaddingValues()
    val top = paddingValues.calculateTopPadding()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        title = {
            Text(text = title, color = Color.White)
        },
        navigationIcon = {
            if (navigationIconAction != null) {
                IconButton(onClick = {
                    navigationIconAction()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        actions = {
            OptionActionMenu(optionActions = optionMenuData)
        })
}

data class OptionMenuData(
    val title: String = "",
    @field:DrawableRes
    val icon: Int = 0,
    val action: (() -> Unit) = {},
    val type: Int = OPTION_MENU_TYPE_HIDDEN
) {
    companion object {
        const val OPTION_MENU_TYPE_HIDDEN = 1
        const val OPTION_MENU_TYPE_ALWAYS_SHOW = 2

        fun iconAction(@DrawableRes icon: Int, action: () -> Unit) =
            OptionMenuData(icon = icon, action = action, type = OPTION_MENU_TYPE_ALWAYS_SHOW)

        fun textAction(title: String, action: () -> Unit) =
            OptionMenuData(title = title, action = action, type = OPTION_MENU_TYPE_HIDDEN)
    }
}

@Preview
@Composable
fun ScaffoldApp(
    title: String = "",
    fabClick: (() -> Unit)? = null,
    optionMenuData: List<OptionMenuData> = emptyList(),
    navigationIconAction: (() -> Unit)? = null,
    customTopBar: @Composable (() -> Unit)? = null,
    appContent: @Composable (() -> Unit) = {},
) {
    Scaffold(
        topBar = {
            customTopBar?.invoke() ?: TopAppBarEx(
                title = title,
                optionMenuData = optionMenuData,
                navigationIconAction = navigationIconAction
            )
        },
        floatingActionButton = {
            if (fabClick != null) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { fabClick.invoke() }) {
                    Icon(Icons.Default.Add, tint = Color.White, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
            appContent()
        }
    }
}