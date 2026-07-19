package com.justwen.androidnga.ui.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview
@Composable
fun TabLayoutWithPager(
    tabs: List<String> = arrayListOf("1", "2"),
    initialPage: Int = 0,
    fixed: Boolean = false,
    content: @Composable ((index: Int) -> Unit)? = null,
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()
    Column {
        if (fixed) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.primary)
            ) {
                TabRowItems(tabs, pagerState, coroutineScope)
            }
        } else {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.primary)
            ) {
                TabRowItems(tabs, pagerState, coroutineScope)
            }
        }
        HorizontalPager(state = pagerState) { pageIndex -> content?.invoke(pageIndex) }
    }

}

@Composable
private fun TabRowItems(
    tabs: List<String> = emptyList(),
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    tabs.forEachIndexed { index, title ->
        Tab(
            selected = pagerState.currentPage == index,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            text = { Text(title) })
    }
}