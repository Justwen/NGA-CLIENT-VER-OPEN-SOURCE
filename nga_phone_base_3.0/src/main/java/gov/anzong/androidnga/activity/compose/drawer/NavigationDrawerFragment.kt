package gov.anzong.androidnga.activity.compose.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.justwen.androidnga.ui.compose.BaseComposeFragment
import com.justwen.androidnga.ui.compose.theme.AppTheme
import com.justwen.androidnga.ui.compose.widget.OptionMenuData
import com.justwen.androidnga.ui.compose.widget.ScaffoldApp
import com.justwen.androidnga.ui.compose.widget.TopAppBarData
import com.justwent.androidnga.bu.UserManager
import gov.anzong.androidnga.R
import gov.anzong.androidnga.activity.compose.board.ForumBoardView
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel
import kotlinx.coroutines.launch
import sp.phone.common.User

class NavigationDrawerFragment : BaseComposeFragment() {

    private val viewModel: NavigationDrawerViewModel by lazy {
        ViewModelProvider(this).get(NavigationDrawerViewModel::class.java)
    }

    private val forumBoardViewModel: ForumBoardViewModel by lazy {
        ViewModelProvider(this).get(ForumBoardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    NavigationDrawerView()
                }
            }
        }
    }

    private fun getTopAppBarData(navigationIconAction: (() -> Unit)? = null): TopAppBarData {
        val topAppBarData = TopAppBarData(title = activity?.title.toString())
        topAppBarData.navigationIconAction = navigationIconAction
        topAppBarData.optionMenuData = getOptionMenuData()
        return topAppBarData
    }

    private fun getOptionMenuData(): List<OptionMenuData> {
        return arrayListOf(
            OptionMenuData(title = "搜索用户", action = {
                viewModel.startSearchActivity(requireActivity())
            }, type = OptionMenuData.OPTION_MENU_TYPE_ALWAYS_SHOW, icon = R.drawable.btn_ic_search),
            OptionMenuData(title = "我的主题", action = {
                viewModel.startPostPage(requireContext(), false)
            }),
            OptionMenuData(title = "我的回复", action = {
                viewModel.startPostPage(requireContext(), true)
            }),
            OptionMenuData(title = "我的缓存", action = {
                viewModel.startCacheTopicPage(requireContext())
            }),
            OptionMenuData(title = "短消息", action = {
                viewModel.startMessagePage(requireContext())
            }),
            OptionMenuData(title = "收藏夹", action = {
                viewModel.startFavoriteTopicPage(requireContext())
            }),
            OptionMenuData(title = "设置", action = {
                viewModel.startSettingsPage(requireActivity())
            }),
        )
    }

    @Composable
    fun UserAvatarView(user: User? = null, userCount: Int = 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable() {
                if (userCount > 0) {
                    viewModel.startProfilePage(requireActivity(), user!!)
                } else {
                    viewModel.startLoginPage(requireActivity())
                }
            },
        ) {

            val avatarPainter: Painter
            val avatarColorFilter: ColorFilter?
            if (user?.avatarUrl?.isNotEmpty() == true) {
                avatarPainter = rememberAsyncImagePainter(
                    model = user.avatarUrl,
                    placeholder = painterResource(id = R.drawable.drawerdefaulticon),
                )
                avatarColorFilter = null
            } else {
                avatarPainter = painterResource(id = R.drawable.drawerdefaulticon)
                avatarColorFilter = ColorFilter.tint(Color.White)
            }
            Image(
                modifier = Modifier
                    .size(width = 55.dp, height = 55.dp)
                    .clip(shape = RoundedCornerShape(27.5.dp)),
                painter = avatarPainter,
                colorFilter = avatarColorFilter,
                contentDescription = ""
            )

            val msg: String
            val subMsg: String
            if (userCount > 1) {
                msg = "已登录${userCount}个账户，点击切换"
                subMsg = "当前：${user?.nickName}(${user?.userId})"
            } else if (userCount == 1) {
                msg = "已登录${userCount}个账户"
                subMsg = "当前：${user?.nickName}(${user?.userId})"
            } else {
                msg = "未登录"
                subMsg = "点击下面的登录账号登录"
            }
            Text(
                text = msg,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = subMsg,
                maxLines = 2,
                modifier = Modifier.padding(top = 6.dp),
                color = Color.White, fontSize = 14.sp
            )
        }
    }


    @Composable
    fun UserHeaderView() {
        var activeIndex by remember { mutableIntStateOf(0) }
        UserManager.getActiveIndexLiveData().observe(requireActivity()) {
            activeIndex = it
        }
        val userList = UserManager.getUserListLiveData().observeAsState(emptyList())
        val userCount = userList.value.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colors.primary)
        ) {
            AnimatedContent(
                targetState = activeIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            ) { index ->
                Box(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (userCount > 0) {
                            val user = userList.value[index % userCount]
                            UserAvatarView(user, userCount)
                        } else {
                            UserAvatarView()
                        }
                    }
                }
            }

            if (userCount > 1) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        onClick = {
                            activeIndex = UserManager.toggleUser(true)
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_action_next_item),
                            tint = Color.White,
                            contentDescription = ""
                        )
                    }
                }
            }
        }

    }

    @Composable
    fun NavigationItem(
        label: String,
        iconId: Int? = null,
        onClick: (() -> Unit)? = null,
        extra: String? = null
    ) {
        NavigationDrawerItem(
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier.alignByBaseline()
                    )

                    if (extra != null) {
                        Text(
                            text = extra,
                            color = Color.Red,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(start = 8.dp)
                        )
                    }
                }

            },
            icon = {
                if (iconId != null) {
                    Box(modifier = Modifier.padding(start = 8.dp, end = 16.dp)) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            painter = painterResource(iconId),
                            tint = Color.DarkGray,
                            contentDescription = ""
                        )
                    }
                }
            },
            selected = false,
            onClick = {
                onClick?.invoke()
            })
    }

    @Composable
    fun NavigationDrawerView() {
        val paddingValues = WindowInsets.statusBars.asPaddingValues()
        val top = paddingValues.calculateTopPadding()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.Transparent,
                    modifier = Modifier.width(280.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .fillMaxHeight()
                    ) {
                        UserHeaderView()
                        HorizontalDivider()
                        NavigationItem(label = "论坛功能")
                        NavigationItem(
                            label = "登录账号",
                            iconId = R.drawable.ic_login,
                            onClick = {
                                viewModel.startLoginPage(requireActivity())
                            })
                        NavigationItem(
                            label = "添加版面ID",
                            iconId = R.drawable.ic_action_add_to_queue,
                            onClick = { viewModel.showAddBoardDialog(this@NavigationDrawerFragment) })
                        NavigationItem(
                            label = "由URL读取",
                            iconId = R.drawable.ic_action_forward,
                            onClick = { viewModel.forwardWithUrl(this@NavigationDrawerFragment) })
                        NavigationItem(
                            label = "清空我的收藏",
                            iconId = R.drawable.ic_action_warning,
                            onClick = { viewModel.showClearFavoriteBoards(requireContext()) })

                        val replyCount = viewModel.replyCount.observeAsState()
                        var extra: String? = null
                        if (replyCount.value != null && replyCount.value!! > 0) {
                            extra = replyCount.value.toString()
                        }
                        NavigationItem(
                            label = "最近被喷",
                            iconId = R.drawable.ic_action_gun,
                            onClick = { viewModel.startNotificationActivity(requireActivity()) },
                            extra = extra
                        )
                        NavigationItem(
                            label = "关于",
                            iconId = R.drawable.ic_action_about,
                            onClick = { viewModel.startAboutNgaClient(requireActivity()) })
                    }

                }
            }, drawerState = drawerState
        ) {
            ScaffoldApp(getTopAppBarData(navigationIconAction = {
                scope.launch {
                    drawerState.open()
                }
            })) {
                ForumBoardView(forumBoardViewModel)
            }
        }
    }


}