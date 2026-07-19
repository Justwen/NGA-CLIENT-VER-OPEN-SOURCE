package com.justwen.androidnga.ui.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.justwen.androidnga.ui.compose.R
import gov.anzong.androidnga.base.util.StringUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun <T : Any> LazyColumnEx(
    columnItem: @Composable (T) -> Unit,
    lazyPagingItems: LazyPagingItems<T>,
    onRefresh: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        val count = lazyPagingItems.itemCount
        if (lazyPagingItems.loadState.refresh is LoadState.Loading && count == 0) {
            item {
                CircularProgressIndicatorView()
            }
        } else if (lazyPagingItems.loadState.hasError) {
            item {
                ErrorIndicatorView(onRefresh)
            }
        } else {
            items(count) {
                val data = lazyPagingItems[it] ?: return@items
                columnItem(data)
                DividerView()
            }
        }
    }
}

@Composable
fun DividerView() {
    Divider(
        modifier = Modifier.padding(
            start = dimensionResource(id = R.dimen.topic_list_item_padding),
            end = dimensionResource(id = R.dimen.topic_list_item_padding)
        ),
        color = Color(0xFFC4BEAE),
        thickness = 0.5.dp
    )
}

@Preview
@Composable
fun CircularProgressIndicatorView(saying: String = StringUtils.getSaying()) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(70.dp),
                color = com.justwen.androidnga.ui.compose.theme.PrimaryGreen,
                strokeWidth = 6.dp
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                text = saying,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(64.dp))
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> PullRefreshColumn(
    columnItem: @Composable (T) -> Unit,
    lazyPagingItems: LazyPagingItems<T>,
    onRefresh: () -> Unit

) {
    var refreshing by remember {
        mutableStateOf(false)
    }

    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val state = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            delay(1000) // 模拟数据加载
            onRefresh.invoke()
            refreshing = false
        }
    })

    Box(modifier = Modifier
        .pullRefresh(state = state)
        .fillMaxSize()) {
        LazyColumnEx(
            columnItem = columnItem,
            lazyPagingItems = lazyPagingItems,
            onRefresh = onRefresh
        )
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun ErrorIndicatorView(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(LocalConfiguration.current.screenHeightDp.dp)
            .clickable {
                onRefresh.invoke()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(id = R.string.error_load_failed))
    }
}