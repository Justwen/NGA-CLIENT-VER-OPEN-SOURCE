package gov.anzong.androidnga.activity.compose.filter

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.justwen.androidnga.ui.compose.BaseComposeFragment
import com.justwen.androidnga.ui.compose.widget.TabLayoutWithPager
import sp.phone.common.User

class FilterWordFragment : BaseComposeFragment() {

    private val viewModel: FilterWordViewModel by lazy {
        ViewModelProvider(this)[FilterWordViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().title = "屏蔽规则"
        viewModel.requestFilterList()
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun ContentView() {
        val tabs = mutableListOf<String>()
        for (item in viewModel.filterStateList) {
            tabs.add(item.title)
        }
        TabLayoutWithPager(tabs = tabs, initialPage = 0, fixed = true) {
            FilterView(viewModel.filterStateList[it])
        }

    }

    @Preview
    @Composable
    fun FilterView(filterState: FilterState<*> = FilterState<String>(title = "屏蔽词")) {
        val userFilters = filterState.filterData.observeAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (filterState.addAction != null) {
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                showAddFilterDialog(filterState.addAction)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "新增",
                            )
                            Text(text = "新增", fontSize = 14.sp)
                        }
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "长按子项可删除", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                if (!filterState.tips.isNullOrEmpty()) {
                    Text(text = filterState.tips!!, color = Color.Gray)
                }
            }
            LazyColumn {
                userFilters.value?.let { it ->
                    items(it.size) { index ->
                        FilterItemView(filterState, it[index]!!, index)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun FilterItemView(filterState: FilterState<*>, data: Any, index: Int) {
        val dataStr = when (data) {
            is User -> data.userId + "/" + data.nickName
            is FilterKeyword -> data.keyword
            else -> data.toString()
        }
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        showDeleteFilterDialog(dataStr, { filterState.removeAction?.invoke(data) })
                    },
                    onClick = {
                        filterState.showAction?.invoke(data)
                    },
                )
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 8.dp), text = dataStr, fontSize = 16.sp
            )
        }
    }

    private fun showAddFilterDialog(action: ((String) -> Unit)?) {
        val editText = EditText(requireContext())
        editText.hint = "如果是用户名，请输入\"用户ID/用户名\"，如果是关键词，请直接输入关键词"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("新增屏蔽项")
            .setView(editText)
            .setPositiveButton("确定") { dialog, which ->
                action?.invoke(editText.text.toString())
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showDeleteFilterDialog(data: String, action: (() -> Unit)?) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("删除屏蔽项")
            .setMessage("确定要删除屏蔽项 “${data}” 吗？")
            .setPositiveButton("确定") { dialog, which ->
                action?.invoke()
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

}