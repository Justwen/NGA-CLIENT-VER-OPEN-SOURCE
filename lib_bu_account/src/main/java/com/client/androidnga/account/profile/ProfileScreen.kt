package com.client.androidnga.account.profile

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.client.androidnga.account.R
import com.justwen.androidnga.ui.compose.widget.CircularProgressIndicatorView
import com.justwen.androidnga.ui.compose.widget.OptionActionMenu
import com.justwen.androidnga.ui.compose.widget.OptionMenuData
import gov.anzong.androidnga.common.view.WebViewEx

@Composable
@Preview
fun ProfileScreen(
    state: ProfileUiState = ProfileUiState(),
    action: (action: ProfileAction) -> Unit = {},
) {
    ImageCollapsingToolbarScreen(state, action, getOptionMenuData(state, action))
}

private fun getOptionMenuData(
    state: ProfileUiState = ProfileUiState(),
    action: (action: ProfileAction) -> Unit,
): List<OptionMenuData> {
    if (state.isLoading || state.isError) return emptyList()
    return buildList {
        if (!state.isCurrentUser) {
            add(
                OptionMenuData(
                    title = "发送短消息",
                    action = { action(ProfileAction.SendMessage(state.uid, state.userName)) })
            )
        }

        add(
            OptionMenuData(
                title = "搜索发帖",
                action = { action(ProfileAction.SearchPost(state.uid, state.userName)) })
        )
        add(
            OptionMenuData(
                title = "搜索回复",
                action = { action(ProfileAction.SearchReply(state.uid, state.userName)) })
        )
        if (state.isCurrentUser) {
            add(
                OptionMenuData(
                    title = "修改头像",
                    action = { action(ProfileAction.ModifyAvatar(state.uid, state.userName)) })
            )
        } else {
            if (state.isFilterUser) {
                add(OptionMenuData(title = "取消屏蔽", action = {
                    action(ProfileAction.ToggleFilterUser(state.uid, state.userName, false))
                }))
            } else {
                add(OptionMenuData(title = "屏蔽用户", action = {
                    action(ProfileAction.ToggleFilterUser(state.uid, state.userName, true))
                }))
            }
        }
        add(
            OptionMenuData(
                title = "通过浏览器打开",
                action = { action(ProfileAction.OpenByBrowser(state.uid, state.userName)) })
        )
    }

}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ImageCollapsingToolbarScreen(
    state: ProfileUiState,
    action: (action: ProfileAction) -> Unit,
    optionMenu: List<OptionMenuData> = emptyList()
) {
    val topAppBarState = rememberTopAppBarState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState
    )

    // LargeTopAppBar 默认可以直接拖动。这里仅禁止顶栏自身响应拖动，
    // nestedScrollConnection 仍由下方列表驱动顶栏展开和收缩。
    val appBarScrollBehavior = remember(scrollBehavior) {
        object : TopAppBarScrollBehavior by scrollBehavior {
            override val isPinned: Boolean = true
        }
    }

    val collapsedFraction = topAppBarState.collapsedFraction
    val titleColor = lerp(
        start = Color.White, stop = Color.White, fraction = collapsedFraction
    )
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                AsyncImage(
                    model = state.avatarUrl,
                    placeholder = painterResource(R.drawable.drawerdefaulticon),
                    error = painterResource(R.drawable.drawerdefaulticon),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            alpha = 1f - collapsedFraction
                            translationY = -40.dp.toPx() * collapsedFraction
                        }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.25f * (1f - collapsedFraction)
                            )
                        )
                )

                ProfileHeaderContent(
                    content = state,
                    collapsedFraction = collapsedFraction,
                    titleColor = titleColor,
                    modifier = Modifier.matchParentSize()
                )

                LargeTopAppBar(
                    title = {
                        Text(
                            text = state.userName,
                            color = titleColor,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            modifier = Modifier.alpha(collapsedFraction),
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { action(ProfileAction.Finish()) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = titleColor
                            )
                        }
                    },
                    actions = {
                        OptionActionMenu(optionMenu)
                    },
                    expandedHeight = 160.dp + statusBarHeight,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    ),
                    scrollBehavior = appBarScrollBehavior
                )
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgressIndicatorView(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    ProfileBasicContent(state)
                }
                item {
                    ProfileSignContent(state, action)
                }
                item {
                    ProfileBoardContent(state, action)
                }
                item {
                    ProfileReputationContent(state)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderContent(
    content: ProfileUiState,
    collapsedFraction: Float,
    titleColor: Color,
    modifier: Modifier = Modifier,
) {
    val expandedFraction = (1f - collapsedFraction).coerceIn(0f, 1f)

    val avatarSize = 65.dp * expandedFraction
    val avatarSpacing = 16.dp * expandedFraction

    val title = content.userName
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = TopAppBarDefaults.TopAppBarExpandedHeight
    val profileContentTop = navigationBarHeight + statusBarHeight

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(top = profileContentTop)
            .alpha(1f - collapsedFraction)
    ) {
        Spacer(
            modifier = Modifier.width(avatarSpacing)
        )

        AsyncImage(
            model = content.avatarUrl,
            placeholder = painterResource(R.drawable.drawerdefaulticon),
            error = painterResource(R.drawable.drawerdefaulticon),
            contentDescription = "用户头像",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(avatarSize)
                .alpha(expandedFraction)
                .clip(CircleShape)
        )

        Spacer(
            modifier = Modifier.width(avatarSpacing)
        )

        Column {
            Text(
                text = title,
                color = titleColor,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(
                modifier = Modifier.height(avatarSpacing)
            )
            Text(
                text = "用户ID：${content.uid}",
                color = titleColor,
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }


    }
}

@Composable
private fun ProfileBasicContent(content: ProfileUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5D7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ":: 基础信息 ::",
                color = Color(0xFF712D08),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ProfileInfoRow(
                left = {
                    ProfileInfoPair(
                        label = "邮箱 : ",
                        value = content.email,
                        modifier = Modifier.weight(1f)
                    )
                },
                right = {
                    ProfileInfoPair(
                        label = "Tel : ",
                        value = content.tel,
                        modifier = Modifier.weight(1f),
                        valueHorizontalArrangement = Arrangement.End
                    )
                }
            )

            ProfileInfoRow(
                left = {
                    ProfileInfoPair(
                        label = "用户组 : ",
                        value = content.userGroup,
                        modifier = Modifier.weight(1f)
                    )
                },
                right = {
                    ProfileInfoPair(
                        label = "发帖数 : ",
                        value = content.posts,
                        modifier = Modifier.weight(1f),
                        valueHorizontalArrangement = Arrangement.End
                    )
                }
            )

            ProfileInfoRow(
                left = {
                    MoneyInfo(
                        money = content.money,
                        modifier = Modifier.weight(1f)
                    )
                },
                right = {
                    var value = "已激活"
                    var color = Color(0xFF3D9F0E)
                    if (content.isNuked) {
                        value = "NUKED(?)"
                        color = Color(0xFFDD0000)
                    } else if (content.isMuted) {
                        value = "已禁言 ${content.muteTime}".trim()
                        color = Color(0xFFFF4500)
                    }
                    ProfileInfoPair(
                        label = "状态 : ",
                        value = value,
                        modifier = Modifier.weight(1f),
                        valueColor = color,
                        valueHorizontalArrangement = Arrangement.End
                    )
                }
            )

            ProfileInfoRow(
                left = {
                    ProfileInfoPair(
                        label = "注册日期 : ",
                        value = content.registerDate,
                        modifier = Modifier.weight(1f)
                    )
                },
                right = {
                    ProfileInfoPair(
                        label = "属地 : ",
                        value = content.loc,
                        modifier = Modifier.weight(1f),
                        valueHorizontalArrangement = Arrangement.End
                    )
                }
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    left: @Composable RowScope.() -> Unit = {},
    right: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        left()
        Spacer(modifier = Modifier.width(8.dp))
        right()
    }
}

@Composable
private fun ProfileInfoPair(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color(0xFF808080),
    valueHorizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = valueHorizontalArrangement
    ) {
        Text(
            text = label,
            color = Color(0xFF121C46),
            maxLines = 1
        )
        Text(
            text = value.ifBlank { "N/A" },
            color = valueColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun MoneyInfo(
    money: String,
    modifier: Modifier = Modifier
) {

    val moneyInt: Int = if (money.isBlank()) 0 else money.toInt()
    val gold: Int = moneyInt / 10000
    val silver: Int = (moneyInt - gold * 10000) / 100
    val copper: Int = (moneyInt - gold * 10000 - silver * 100)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "金钱 : ",
            color = Color(0xFF121C46),
            maxLines = 1
        )
        Text(
            text = gold.toString(),
            color = Color(0xFF808080),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Image(
            painter = painterResource(R.drawable.gold),
            contentDescription = "金币",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = silver.toString(),
            color = Color(0xFF808080),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Image(
            painter = painterResource(R.drawable.silver),
            contentDescription = "银币",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = copper.toString(),
            color = Color(0xFF808080),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Image(
            painter = painterResource(R.drawable.copper),
            contentDescription = "铜币",
            modifier = Modifier.size(16.dp)
        )
    }
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
@UiComposable
private fun ProfileSignContent(
    content: ProfileUiState,
    action: (action: ProfileAction) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5D7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileInfoRow(
                left = {
                    Text(
                        text = ":: 签名 ::",
                        color = Color(0xFF712D08),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                },
                right = {
                    if (content.isCurrentUser) {
                        Text(
                            text = "[更改签名]",
                            color = Color(0xFF712D08),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.clickable {
                                action(ProfileAction.ModifySign(sign = content.signRaw))
                            },
                        )
                    }
                }
            )

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                factory = { context ->
                    WebViewEx(context).apply {
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false
                        settings.javaScriptEnabled = false
                        settings.allowFileAccess = true
                        settings.allowContentAccess = false
                    }
                },
                update = { webView ->
                    if (webView.tag != content.sign) {
                        webView.loadDataWithBaseURL(
                            "file:///android_asset/",
                            content.sign,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webView.tag = content.sign
                    }
                },
                onRelease = { webView ->
                    // destroy() 要求 view 已脱离 view system，否则会打 warning 甚至抛异常
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    webView.stopLoading()
                    webView.destroy()
                }
            )

        }
    }
}

@Composable
private fun ProfileReputationContent(content: ProfileUiState) {
    val (userReputationItems, regularReputationItems) = content.reputation.partition {
        it.name.startsWith("用户") && it.name.endsWith("的声望")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5D7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ":: 声望 ::",
                color = Color(0xFF712D08),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "表示与 论坛/某版面/某用户 的关系",
                color = Color(0xFF808080),
                style = MaterialTheme.typography.bodyMedium
            )

            regularReputationItems.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    rowItems.forEach { item ->
                        ReputationItem(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            userReputationItems.forEach { item ->
                ReputationItem(
                    item = item,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ReputationItem(
    item: ProfileUiState.Reputation,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFF121C46))) {
                append(item.name)
            }
            withStyle(SpanStyle(color = Color(0xFF808080))) {
                append(":")
                append(item.value.ifBlank { "N/A" })
            }
        },
        modifier = modifier.padding(end = 4.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileBoardContent(
    content: ProfileUiState,
    action: (ProfileAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5D7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ":: 管理权限 ::",
                color = Color(0xFF712D08),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "在以下版面担任版主",
                color = Color(0xFF808080),
                style = MaterialTheme.typography.bodyMedium
            )

            if (content.adminForums.isEmpty()) {
                Text(
                    text = "无管理板块",
                    color = Color(0xFF808080),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    content.adminForums.forEach { forum ->
                        Text(
                            text = "[${forum.name}]",
                            color = Color(0xFF551200),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                action(
                                    ProfileAction.OpenForum(
                                        fid = forum.fid,
                                        forumName = forum.name
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


