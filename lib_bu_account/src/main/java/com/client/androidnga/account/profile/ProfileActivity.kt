package com.client.androidnga.account.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.client.androidnga.ui.compose3.theme.AppTheme
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.base.service.api.IThemeManagerService
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import gov.anzong.androidnga.base.util.ToastUtils
import kotlinx.coroutines.launch
import sp.phone.param.ParamKey
import java.net.URLEncoder

@Route(path = ARouterConstants.ACTIVITY_PROFILE)
class ProfileActivity : BaseComposeActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 页面内的跳转、Toast 等一次性副作用统一在这里消费，ViewModel 只负责分发
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { onEvent(it) }
            }
        }
    }

    private fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Navigate -> onAction(event.action)
            is ProfileEvent.Toast -> ToastUtils.success(event.msg)
        }
    }

    @Composable
    override fun ContentView() {
    }

    override fun buildContentView() {
        val index =
            ARouter.getInstance().navigation(IThemeManagerService::class.java).getThemeIndex()
        setContent {
            AppTheme(themeIndex = index) {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    action = { viewModel.onAction(it) },
                )
            }

        }
    }

    private fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.SendMessage -> sendShortMessage(action.userName)
            is ProfileAction.SearchPost -> searchPost(action.uid, action.userName)
            is ProfileAction.SearchReply -> searchReply(action.uid, action.userName)
            is ProfileAction.ModifyAvatar -> startModifyAvatar()
            is ProfileAction.Finish -> finish()
            is ProfileAction.ModifySign -> startModifySign(action.sign)
            is ProfileAction.OpenForum -> openForum(action.fid, action.forumName)
            is ProfileAction.OpenByBrowser -> openByBrowser(action.uid, action.userName)
            // 屏蔽属状态类意图，已在 ViewModel 内闭环，不会下发到这里
            is ProfileAction.ToggleFilterUser -> Unit
        }
    }

    private fun sendShortMessage(userName: String) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_MESSAGE_POST)
            .withString("to", userName)
            .withString(ParamKey.KEY_ACTION, "new")
            .withString("messagemode", "yes")
            .navigation(this)
    }

    private fun openByBrowser(uid: String, userName: String) {
        val url =
            "https://bbs.ngacn.cc/nuke.php?func=ucp&uid=${uid}&username=${
                URLEncoder.encode(
                    userName,
                    "gbk"
                )
            }"
        val intent = Intent().apply {
            action = "android.intent.action.VIEW"
            data = url.toUri()
        }
        startActivity(intent)
    }

    //todo 需要用路由的方式
    private fun startModifyAvatar() {
        val intent = Intent()
        intent.setClassName(packageName, "gov.anzong.androidnga.activity.AvatarPostActivity")
        startActivity(intent)
    }

    private fun openForum(fid: Int, forumName: String) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_THREAD_LIST)
            .withInt(ParamKey.KEY_FID, fid)
            .withString(ParamKey.KEY_TITLE, forumName)
            .navigation(this)
    }

    private fun searchPost(uid: String, userName: String) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_THREAD_LIST)
            .withInt(ParamKey.KEY_AUTHOR_ID, uid.toInt())
            .withString(ParamKey.KEY_AUTHOR, userName)
            .navigation(this)
    }

    private fun searchReply(uid: String, userName: String) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_THREAD_LIST)
            .withInt(ParamKey.KEY_AUTHOR_ID, uid.toInt())
            .withString(ParamKey.KEY_AUTHOR, userName)
            .withInt(ParamKey.KEY_SEARCH_POST, 1)
            .navigation(this)
    }

    private fun startModifySign(sign: String) {
        val intent = Intent()
        intent.putExtra("prefix", sign)
        intent.setClassName(packageName, "gov.anzong.androidnga.activity.SignPostActivity")
        startActivity(intent)
    }

}