package gov.anzong.androidnga.activity.compose.drawer

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.justwent.androidnga.bu.UserManager
import gov.anzong.androidnga.R
import gov.anzong.androidnga.activity.AboutActivity
import gov.anzong.androidnga.activity.SettingsActivity
import gov.anzong.androidnga.activity.TopicCacheActivity
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel.addBookmarkBoard
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel.removeAllBookmarkBoard
import gov.anzong.androidnga.arouter.ARouterConstants
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.common.PreferenceKey
import sp.phone.common.User
import sp.phone.param.ParamKey
import sp.phone.ui.fragment.dialog.AddBoardDialogFragment
import sp.phone.ui.fragment.dialog.UrlInputDialogFragment
import sp.phone.util.ARouterUtils
import sp.phone.util.ActivityUtils

class NavigationDrawerViewModel : ViewModel() {

    val replyCount: MutableLiveData<Int> = MutableLiveData()

    init {
        replyCount.value = PreferenceUtils.getData(PreferenceKey.KEY_REPLY_COUNT, 0)
        PreferenceUtils.getDefaultPreferences()
            .registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == PreferenceKey.KEY_REPLY_COUNT) {
                    replyCount.postValue(sharedPreferences.getInt(key, 0))
                }
            }
    }

    fun startLoginPage(activity: Activity) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_LOGIN)
            .navigation(
                activity, 1
            )
    }

    fun startProfilePage(activity: Activity, user: User) {
        ARouterUtils
            .build(ARouterConstants.ACTIVITY_PROFILE)
            .withString("mode", "uid")
            .withString("uid", user.userId)
            .navigation()
    }

    fun showClearFavoriteBoards(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("是否要清空我的收藏？")
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(
                R.string.ok
            ) { dialog: DialogInterface?, which: Int ->
                removeAllBookmarkBoard()
            }
            .create()
            .show()
    }

    fun showAddBoardDialog(fragment: Fragment) {
        AddBoardDialogFragment().setOnAddBookmarkListener { name, fid, stid ->
            addBookmarkBoard(
                name, fid, stid
            )
        }.show(fragment.getChildFragmentManager())
    }

    fun forwardWithUrl(fragment: Fragment) {
        UrlInputDialogFragment().show(fragment.childFragmentManager)
    }

    fun startAboutNgaClient(activity: Activity) {
        activity.startActivity(Intent(activity, AboutActivity::class.java))
    }

    fun startNotificationActivity(activity: Activity) {
        ARouterUtils
            .build(ARouterConstants.ACTIVITY_NOTIFICATION)
            .navigation(activity)
    }

    fun startSearchActivity(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_SEARCH)
            .navigation(activity)
    }

    fun startPostPage(context: Context, isReply: Boolean) {
        val user = UserManager.getActiveUser()
        val userName = user?.nickName ?: ""
        val uid = user?.userId?.toInt() ?: 0
        val postcard = ARouterUtils
            .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
            .withInt(ParamKey.KEY_AUTHOR_ID, uid)
            .withString(ParamKey.KEY_AUTHOR, userName)
        if (isReply) {
            postcard.withInt(ParamKey.KEY_SEARCH_POST, 1)
        }
        postcard.navigation(context)
    }

    fun startCacheTopicPage(context: Context) {
        startActivity(context, Intent(context, TopicCacheActivity::class.java), null)
    }

    fun startMessagePage(context: Context) {
        ARouterUtils
            .build(ARouterConstants.ACTIVITY_MESSAGE_LIST)
            .navigation(context)
    }

    fun startFavoriteTopicPage(context: Context) {
        ARouterUtils
            .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
            .withInt(ParamKey.KEY_FAVOR, 1)
            .navigation(context)
    }

    fun startSettingsPage(activity: Activity) {
        val intent = Intent()
        intent.setClass(activity, SettingsActivity::class.java)
        startActivityForResult(activity, intent, ActivityUtils.REQUEST_CODE_SETTING, null)
    }
}