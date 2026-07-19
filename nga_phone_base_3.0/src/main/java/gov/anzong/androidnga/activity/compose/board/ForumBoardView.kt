package gov.anzong.androidnga.activity.compose.board

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.justwen.androidnga.ui.compose.widget.TabLayoutWithPager
import com.justwent.androidnga.bu.UserManager
import gov.anzong.androidnga.R
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.core.board.data.BoardEntity
import sp.phone.common.ApiConstants
import kotlin.math.abs


@Composable
fun ForumBoardView(forumBoardViewModel: ForumBoardViewModel) {
    val boardData by forumBoardViewModel.boardLiveData.observeAsState()
    val tabs = arrayListOf<String>()
    boardData?.let {
        it.forEach {
            tabs.add(it.name)
        }
        val initialPage = if (forumBoardViewModel.bookmarkSizeLiveData.value!! > 0) 0 else 1
        TabLayoutWithPager(tabs = tabs, initialPage = initialPage) {
            ForumBoardContent(it, forumBoardViewModel)
        }
    }
}

@Composable
fun ForumBoardGroupView(board: BoardEntity, context: Context = ContextUtils.getContext()) {
    Row(modifier = Modifier.padding(16.dp)) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.default_board_icon),
            contentDescription = "",
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = board.name,
            color = Color(context.resources.getColor(R.color.text_color, null)),
        )
    }
}

@Composable
private fun ForumBoardGridItemView(
    child: BoardEntity,
    forumBoardViewModel: ForumBoardViewModel,
    context: Context = ContextUtils.getContext()
) {
    val paddingValue = 4.dp
    val imageSize = 48.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(paddingValue)
            .fillMaxWidth()
            .clickable {
                forumBoardViewModel.showTopicList(child)
            }
    ) {
        Spacer(modifier = Modifier.height(paddingValue))
        val resId = getResId(child)
        if (resId > 0) {
            Image(
                modifier = Modifier.size(imageSize),
                painter = painterResource(id = resId),
                contentDescription = ""
            )
        } else {
            val url = getResUrl(child)
            Image(
                modifier = Modifier.size(imageSize),
                painter = rememberAsyncImagePainter(
                    model = url,
                    placeholder = painterResource(id = R.drawable.default_board_icon),
                    error = painterResource(id = R.drawable.default_board_icon)
                ),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier
                .padding(top = paddingValue, bottom = paddingValue),
            color = Color(context.resources.getColor(R.color.text_color, null)),
            text = child.name
        )
        Spacer(modifier = Modifier.height(paddingValue))
    }
}

private fun getResUrl(board: BoardEntity): String {
    val url = if (board.stid != 0) {
        String.format(ApiConstants.URL_BOARD_ICON_STID, board.stid)
    } else {
        String.format(ApiConstants.URL_BOARD_ICON, board.fid)
    }
    return url
}


private fun getResId(board: BoardEntity): Int {
    if (board.stid != 0) {
        return 0
    }

    val fid = board.fid
    val resName = if (fid > 0) "p$fid" else "p_" + abs(fid)
    return ContextUtils.getResources()
        .getIdentifier(resName, "drawable", ContextUtils.getContext().packageName)
}

@Composable
fun ForumBoardBookmarkContent(bookmark: BoardEntity, forumBoardViewModel: ForumBoardViewModel) {
    val bookmarkSize by forumBoardViewModel.bookmarkSizeLiveData.observeAsState()
    val maxColumn = 3

    Column (Modifier.fillMaxSize()) {

        if (UserManager.getUserList().size == 1) {
            Text(modifier = Modifier.padding(8.dp), text = "建议登录多个账号，可有效改善跳转系统浏览器问题")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(maxColumn),
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            items(bookmarkSize!!) { index ->
                ForumBoardGridItemView(bookmark.children!![index], forumBoardViewModel)
            }
            item(span = { GridItemSpan(maxColumn) }) {
                val paddingValues = WindowInsets.navigationBars.asPaddingValues()
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun ForumBoardContent(index: Int, forumBoardViewModel: ForumBoardViewModel) {
    val boardData = forumBoardViewModel.getBoardData(index)
    if (boardData.type == BoardEntity.BoardType.BOOKMARK) {
        ForumBoardBookmarkContent(boardData, forumBoardViewModel)
    } else {
        val boardList: List<BoardEntity> = forumBoardViewModel.getBoardData(index).children!!
        val maxColumn = 3
        LazyVerticalGrid(
            columns = GridCells.Fixed(maxColumn),
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            boardList.forEach {
                if (it.type == BoardEntity.BoardType.GROUP) {
                    item(span = { GridItemSpan(maxColumn) }) {
                        ForumBoardGroupView(it)
                    }
                    it.children?.let { data ->
                        items(data.size) { index ->
                            ForumBoardGridItemView(data[index], forumBoardViewModel)
                        }
                    }
                } else {
                    item {
                        ForumBoardGridItemView(it, forumBoardViewModel)
                    }
                }
            }
            item(span = { GridItemSpan(maxColumn) }) {
                val paddingValues = WindowInsets.navigationBars.asPaddingValues()
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}


