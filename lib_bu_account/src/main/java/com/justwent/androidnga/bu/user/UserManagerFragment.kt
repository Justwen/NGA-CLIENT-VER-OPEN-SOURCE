package com.justwent.androidnga.bu.user

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alibaba.android.arouter.launcher.ARouter
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.module.account.R
import com.justwen.androidnga.ui.compose.BaseComposeFragment
import com.justwent.androidnga.bu.UserManager
import sp.phone.common.User

class UserManagerFragment : BaseComposeFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().setTitle("账号管理")
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun ContentView() {
        val userListState = UserManager.getUserListLiveData().observeAsState(emptyList())
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AddAccountItem()
            LazyColumn {
                val userList = userListState.value
                items(userList.size) { index ->
                    AccountItem(userList[index], index)
                }
            }
        }
    }

    @Composable
    private fun AddAccountItem() {
        Box(modifier = Modifier.clickable {
            ARouter.getInstance().build(ARouterConstants.ACTIVITY_LOGIN).navigation()
        }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新增",
                )
                Text(text = "登录新账号")
            }
        }

    }


    @Composable
    private fun AccountItem(user: User, index: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        showUserProfile(user)
                    }
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val avatarPainter: Painter = rememberAsyncImagePainter(
                    model = user.avatarUrl ?: "",
                    error = painterResource(id = R.drawable.drawerdefaulticon),
                    placeholder = painterResource(id = R.drawable.drawerdefaulticon),
                )
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(shape = RoundedCornerShape(15.dp)),
                    painter = avatarPainter,
                    contentDescription = ""
                )
                Text(
                    text = user.toShortString(),
                    maxLines = 1,
                    modifier = Modifier.padding(end = 32.dp)
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(onClick = {
                UserManager.removeUser(index)
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "新增",
                )
            }

        }

    }

    private fun showUserProfile(user: User) {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_PROFILE)
            .withString("mode", "username").withString("username", user.mNickName).navigation()
    }


}