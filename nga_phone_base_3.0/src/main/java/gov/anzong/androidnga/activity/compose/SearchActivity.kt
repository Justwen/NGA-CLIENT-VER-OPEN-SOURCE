package gov.anzong.androidnga.activity.compose

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import com.justwen.androidnga.ui.compose.widget.TopAppBarData
import gov.anzong.androidnga.arouter.ARouterConstants

@Route(path = ARouterConstants.ACTIVITY_SEARCH)
class SearchActivity : BaseComposeActivity() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fid = intent.getIntExtra("fid", 0)
        super.onCreate(savedInstanceState)
    }

    override fun getTopAppBarData(): TopAppBarData {
        val topAppBarData = TopAppBarData(title = title.toString())
        topAppBarData.optionMenuData = getOptionMenuData()
        topAppBarData.navigationIconAction = { finish() }
        topAppBarData.customTopBar = { SearchEditView() }
        return topAppBarData
    }

    @Composable
    fun SearchEditView() {
        var searchText by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, end = 16.dp)
                .fillMaxSize()
        ) {
            BasicTextField(value = searchText,
                onValueChange = {
                    searchText = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.query(this@SearchActivity, searchText)
                }),
                singleLine = true,
                modifier = Modifier
                    .background(Color.White, shape = CutCornerShape(2.dp))
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .wrapContentHeight(Alignment.CenterVertically),
                decorationBox = {
                    if (searchText.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            val searchMode by viewModel.searchMode.observeAsState()
                            Text(
                                text = viewModel.getSearchTintText(searchMode!!),
                                color = Color.Gray,
                                style = TextStyle.Default
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clickable(onClick = { searchText = "" })
                                    .fillMaxHeight(),
                                tint = Color.Gray,
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""
                            )

                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart
                    ) {
                        it()
                    }

                })
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }


    @Composable
    override fun ContentView() {
        val tabData = viewModel.searchData
        var tabIndex by remember { mutableIntStateOf(0) }

        Column {
            TabRow(selectedTabIndex = tabIndex, backgroundColor = Color.Transparent) {
                tabData.forEachIndexed { index, data ->
                    Tab(modifier = Modifier.height(40.dp), selected = tabIndex == index, onClick = {
                        tabIndex = index
                        viewModel.searchMode.value = data.second
                    }) { Text(data.first) }
                }

            }
            SearchOptionView(tabIndex)
            SearchHistoryView()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SearchHistoryView() {
        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "搜索历史",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            val keyList by viewModel.keyList.observeAsState(emptyList())
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                keyList.forEach {
                    SearchHistoryItemView(it)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

        }
    }

    @Preview
    @Composable
    fun SearchHistoryItemView(text: String = "强撸灰飞烟灭", deleteMode: Boolean = false) {
        Box(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 4.dp)) {
            Row(
                Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .height(30.dp)
                    .clickable(onClick = { viewModel.query(this@SearchActivity, text) })
                    .padding(all = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(), color = Color.Gray
                )
                Spacer(modifier = Modifier
                    .width(8.dp)
                    .background(Color.Gray))
                Icon(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(7.dp))
                        .clickable(onClick = { viewModel.deleteHistory(text) }),
                    tint = Color.White,
                    imageVector = Icons.Default.Close,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }


    }

    @Composable
    fun SearchOptionView(index: Int) {
        when (viewModel.searchData[index].second) {
            SearchViewModel.SEARCH_MODE_TOPIC -> {
                SearchTopicOptionView()
            }

            SearchViewModel.SEARCH_MODE_BOARD -> {
                SearchBoardOptionView()
            }

            SearchViewModel.SEARCH_MODE_USER -> {
                SearchUserOptionView()
            }
        }
    }

    @Composable
    fun SearchUserOptionView() {
        var modeIndex by remember { mutableIntStateOf(0) }
        val modeData = viewModel.searchUserData

        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                text = "搜索选项",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                modeData.forEachIndexed { index, pair ->
                    Row(
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                modeIndex = index
                                viewModel.searchUserMode = pair.second
                            }), verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = index == modeIndex, onClick = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pair.first,
                            fontSize = 12.sp,
                            modifier = Modifier.wrapContentSize(
                                Alignment.CenterStart
                            )
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                }
            }
        }

    }


    @Composable
    fun SearchTopicOptionView() {
        var modeIndex by remember { mutableIntStateOf(0) }
        val modeData = viewModel.searchTopicData

        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                text = "搜索选项",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                modeData.forEachIndexed { index, pair ->
                    Row(
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                modeIndex = index
                                viewModel.searchTopicMode = pair.second
                            }), verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = index == modeIndex, onClick = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pair.first,
                            fontSize = 12.sp,
                            modifier = Modifier.wrapContentSize(
                                Alignment.CenterStart
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                var withContent by remember { mutableStateOf(viewModel.searchTopicWithContent) }

                Row(
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            withContent = !withContent
                            viewModel.searchTopicWithContent = withContent
                        }), verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(checked = withContent, onCheckedChange = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "包括正文", fontSize = 12.sp, modifier = Modifier.wrapContentSize(
                            Alignment.CenterStart
                        )
                    )
                }

            }
        }

    }

    @Composable
    fun SearchBoardOptionView() {

    }
}