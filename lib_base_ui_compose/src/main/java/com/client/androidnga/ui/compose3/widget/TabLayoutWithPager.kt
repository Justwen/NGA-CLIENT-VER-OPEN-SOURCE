package com.client.androidnga.ui.compose3.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Tab 行 + 横向 Pager 的组合。
 *
 * 两个"能不能滚"是独立的，别混：
 * - [tabScrollable]：**上面 Tab 条**本身能不能横向滚动。
 * - [swipeEnabled]：**下面内容页**能不能用手指左右滑动切换 Tab（关了之后只能点 Tab 切换）。
 *
 * Tab 条的排列方式由 [tabScrollable] 决定，两者互斥：
 * - 不可滚动（`tabScrollable = false`）：tab 等分整行宽度，永不滚动。适合 tab 数量固定且少（2~4 个）。
 * - 可滚动（`tabScrollable = true`）：tab 按内容宽度排布（最小 [minTabWidth]），总宽超出屏幕时可横向滚动。
 *   注意内容不足一屏时也不会撑满，而是左对齐、右侧留白。适合 tab 多、标题长短不一。
 *
 * @param tabs tab 标题集合
 * @param initialPage 初始选中页；超出 [tabs] 范围时由 Pager 自行收敛
 * @param tabScrollable Tab 条是否可横向滚动。false 为等分整行不可滚，true 为按内容宽度可滚
 * @param swipeEnabled 内容页是否可通过手势左右滑动切换；false 时仅能点击 Tab 切换（Tab 点击仍生效）
 * @param edgePadding Tab 条可滚动时首个 tab 与左侧、末个 tab 与右侧的留白；等分模式下无效
 * @param minTabWidth Tab 条可滚动时单个 tab 的最小宽度，避免短标题挤成一团；等分模式下无效
 * @param content 每个 page 的内容
 */
@Preview
@Composable
fun TabLayoutWithPager(
    tabs: List<String> = arrayListOf("1", "2"),
    initialPage: Int = 0,
    tabScrollable: Boolean = false,
    swipeEnabled: Boolean = true,
    edgePadding: Dp = 0.dp,
    minTabWidth: Dp = TabRowDefaults.ScrollableTabRowMinTabWidth,
    content: @Composable ((index: Int) -> Unit)? = null,
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()
    Column {
        if (tabScrollable) {
            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primary,
                edgePadding = edgePadding,
                minTabWidth = minTabWidth,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(pagerState.currentPage),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
            ) {
                TabRowItems(tabs, pagerState, coroutineScope)
            }
        } else {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(pagerState.currentPage),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
            ) {
                TabRowItems(tabs, pagerState, coroutineScope)
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = swipeEnabled,
        ) { pageIndex -> content?.invoke(pageIndex) }
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
            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
            unselectedContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            text = { Text(title) })
    }
}