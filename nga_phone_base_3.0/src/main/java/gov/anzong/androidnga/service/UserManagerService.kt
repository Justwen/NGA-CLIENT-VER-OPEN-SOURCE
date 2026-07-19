package gov.anzong.androidnga.service

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.service.api.IUserManagerService
import gov.anzong.androidnga.base.util.ToastUtils
import sp.phone.common.UserManagerImpl

@Route(path = IUserManagerService.ROUTER_PATH)
class UserManagerService : IUserManagerService {

    override fun showUserSwitchDialog(context: Context, callback: (() -> Unit)?) {
        val users = UserManagerImpl.getInstance().userList
        if (users.isNullOrEmpty()) {
            return
        }
        val index = UserManagerImpl.getInstance().activeUserIndex
        val items = Array<CharSequence>(users.size) { i -> users[i].nickName }

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setSingleChoiceItems(
            items, index
        ) { dialog, which ->
            run {
                UserManagerImpl.getInstance().setActiveUser(which)
                ToastUtils.info(
                    "切换账户成功,当前账户名:" + items[index]
                )
                dialog.dismiss()
                callback?.invoke()
            }
        }.setTitle("切换账号")
        dialogBuilder.show()
    }

    override fun addUser(uid: String, cid: String, name: String) {
        UserManagerImpl.getInstance().addUser(uid, cid, name)
    }

    override fun init(context: Context?) {
    }

}