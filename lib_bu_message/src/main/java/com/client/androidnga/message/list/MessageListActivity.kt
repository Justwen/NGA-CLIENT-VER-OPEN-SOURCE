package com.client.androidnga.message.list

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.base.service.api.IUserManagerService
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import com.justwen.androidnga.ui.compose.widget.OptionMenuData

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_LIST)
class MessageListActivity : BaseComposeActivity() {

    private val viewModel: MessageListViewModel by lazy {
        ViewModelProvider(this)[MessageListViewModel::class.java]
    }

    override fun getOptionMenuData(): List<OptionMenuData> {
        val accountSwitch = OptionMenuData(title = "切换账号", action = {
            showAccountSwitchDialog()
        })
        return arrayListOf(accountSwitch)
    }

    private fun showAccountSwitchDialog() {
        val userManager = ARouter.getInstance().navigation(IUserManagerService::class.java)
        userManager.showUserSwitchDialog(this)
    }

    override fun getFabClickAction(): () -> Unit {
        return {
            ARouter.getInstance().build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                .withString("action", "new")
                .navigation(this)
        }
    }

    @Composable
    override fun ContentView() {
        MessageListScreen(viewModel, onMessageClick = ::startMessageDetail)
    }

    private fun startMessageDetail(mid: String, subject: String, recipient: String) {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_MESSAGE_DETAIL)
            .withString("mid", mid)
            .withString("subject", subject)
            .withString("recipient", recipient)
            .navigation(this)
    }

}

