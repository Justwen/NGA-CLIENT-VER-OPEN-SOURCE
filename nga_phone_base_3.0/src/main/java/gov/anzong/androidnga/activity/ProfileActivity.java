package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.DeviceUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumDecoder;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.bean.AdminForumsData;
import sp.phone.http.bean.ProfileData;
import sp.phone.http.bean.ReputationData;
import sp.phone.param.ParamKey;
import sp.phone.task.JsonProfileLoadTask;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.webview.WebViewEx;

@Route(path = ARouterConstants.ACTIVITY_PROFILE)
public class ProfileActivity extends BaseActivity implements OnHttpCallBack<ProfileData> {

    private static final String TAG = "ProfileActivity";

    private ProfileData mProfileData;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private String mParams;

    private boolean mCurrentUser;

    @BindView(R.id.tv_user_money_copper)
    public TextView mMoneyCopperTv;

    @BindView(R.id.tv_user_money_silver)
    public TextView mMoneySilverTv;

    @BindView(R.id.tv_user_money_gold)
    public TextView mMoneyGoldTv;

    @BindView(R.id.tv_user_state)
    public TextView mUserStateTv;

    @BindView(R.id.tv_user_mute_time)
    public TextView mUserMuteTime;

    @BindView(R.id.tv_user_group)
    public TextView mUserGroupTv;

    @BindView(R.id.tv_user_register_time)
    public TextView mRegisterTimeTv;

    @BindView(R.id.tv_user_tel)
    public TextView mUserTelTv;

    @BindView(R.id.tv_user_email)
    public TextView mUserEmailTv;

    @BindView(R.id.tv_post_count)
    public TextView mPostCountTv;

    @BindView(R.id.btn_modify_sign)
    public TextView mModifySignBtn;

    @BindView(R.id.iv_avatar)
    public ImageView mAvatarIv;

    @BindView(R.id.toolbar_layout)
    public CollapsingToolbarLayout mToolbarLayout;

    @BindView(R.id.tv_uid)
    public TextView mUidTv;

    @BindView(R.id.wv_admin)
    public WebViewEx mAdminWebView;

    @BindView(R.id.wv_fame)
    public WebViewEx mFameWebView;

    @BindView(R.id.wv_sign)
    public WebViewEx mSignWebView;

    private JsonProfileLoadTask mProfileLoadTask;

    private Menu mOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        UserManager um = UserManagerImpl.getInstance();
        if (intent.hasExtra("uid")) {
            String uid = intent.getStringExtra("uid");
            mCurrentUser = uid.equals(um.getUserId());
            mParams = "uid=" + uid;
        } else if (intent.hasExtra("username")) {
            String userName = intent.getStringExtra("username");
            mCurrentUser = userName.endsWith(um.getUserName());
            if (userName.startsWith("UID")) {
                mParams = "uid=" + userName.substring(3);
            } else {
                mParams = "username=" + StringUtils.encodeUrl(userName, "gbk");
            }
        }

        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setupActionBar();
        refresh();
    }

    private void refresh() {
        ActivityUtils.getInstance().noticeSaying(this);
        mProfileLoadTask = new JsonProfileLoadTask(this);
        mProfileLoadTask.execute(mParams);
    }


    @Override
    protected void onDestroy() {
        mProfileLoadTask.cancel();
        super.onDestroy();
    }

    private void loadBasicProfile(ProfileData profileInfo) {
        mToolbarLayout.setTitle(profileInfo.getUserName());
        mUidTv.setText(String.format("用户ID : %s", profileInfo.getUid()));
        mPostCountTv.setText(profileInfo.getPostCount());
        mRegisterTimeTv.setText(profileInfo.getRegisterDate());
        mUserEmailTv.setText(profileInfo.getEmailAddress());
        mUserTelTv.setText(profileInfo.getPhoneNumber());
        mUserGroupTv.setText(profileInfo.getMemberGroup());
        TextView ipLocView = findViewById(R.id.tv_user_iploc);
        if (ipLocView != null && profileInfo.ipLoc != null) {
            ipLocView.setText(profileInfo.ipLoc);
        }
        if (mCurrentUser) {
            mModifySignBtn.setVisibility(View.VISIBLE);
        } else {
            mModifySignBtn.setVisibility(View.GONE);
        }
        handleAvatar(profileInfo);
        handleUserState(profileInfo);
        handleUserMoney(profileInfo);
    }

    private void handleUserState(ProfileData profileInfo) {
        if (profileInfo.isMuted()) {
            mUserStateTv.setText("已禁言");
            mUserStateTv.setTextColor(ContextCompat.getColor(this, R.color.color_state_muted));
            if (!StringUtils.isEmpty(profileInfo.getMutedTime())) {
                mUserMuteTime.setText(profileInfo.getMutedTime());
                //  mUserMuteTime.setVisibility(View.VISIBLE);
            }
        } else if (profileInfo.isNuked()) {
            mUserStateTv.setText("NUKED(?)");
            mUserStateTv.setTextColor(ContextCompat.getColor(this, R.color.color_state_nuked));
        } else {
            mUserStateTv.setText("已激活");
            mUserStateTv.setTextColor(ContextCompat.getColor(this, R.color.color_state_active));
        }
    }

    private void handleUserMoney(ProfileData profileInfo) {

        int money = Integer.parseInt(profileInfo.getMoney());
        int gold = money / 10000;
        int silver = (money - gold * 10000) / 100;
        int copper = (money - gold * 10000 - silver * 100);
        mMoneyGoldTv.setText(String.valueOf(gold));
        mMoneySilverTv.setText(String.valueOf(silver));
        mMoneyCopperTv.setText(String.valueOf(copper));
    }

    protected String getUrl() {
        return "http://bbs.ngacn.cc/nuke.php?func=ucp&" + mParams;
    }

    private String createAdminHtml(ProfileData ret) {
        StringBuilder builder = new StringBuilder();
        List<AdminForumsData> adminForumsEntryList = ret.getAdminForums();
        if (adminForumsEntryList == null) {
            builder.append("无管理板块");
        } else {
            for (AdminForumsData data : adminForumsEntryList) {
                builder.append("<a style=\"color:#551200;\" href=\"http://nga.178.com/thread.php?fid=")
                        .append(data.getFid()).append("\">[")
                        .append(data.getForumName())
                        .append("]</a>&nbsp;");
            }
            builder.append("<br>");
        }
        return builder.toString();
    }

    @OnClick(R.id.btn_modify_sign)
    public void startChangeSignActivity() {
        Intent intent = new Intent();
        intent.putExtra("prefix", mProfileData.getSign());
        intent.setClass(this, PhoneConfiguration.getInstance().signPostActivityClass);
        startActivityForResult(intent, 321);
    }

    private void loadProfileInfo(ProfileData profileInfo) {
        loadBasicProfile(profileInfo);
        handleSignWebView(mSignWebView, profileInfo);
        handleAdminWebView(mAdminWebView, profileInfo);
        handleFameWebView(mFameWebView, profileInfo);
        if (mOptionMenu != null) {
            onPrepareOptionsMenu(mOptionMenu);
        }
    }

    private String createFameHtml(ProfileData ret, String color) {
        String frame = ret.getFrame();
        try {
            frame = String.valueOf(Double.parseDouble(ret.getFrame()) / 10.0d);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder("<ul style=\"padding: 0px; margin: 0px;\">");
        builder.append("<li style=\"display: block;float: left;width: 33%;\">")
                .append("<label style=\"float: left;color: ").append(color).append(";\">威望</label>")
                .append("<span style=\"float: left; color: #808080;\">:</span>")
                .append("<span style=\"float: left; color: #808080;\">")
                .append(frame)
                .append("</span></li>");
        List<ReputationData> reputationEntryList = ret.getReputationEntryList();
        if (reputationEntryList != null) {
            for (ReputationData data : reputationEntryList) {
                builder.append("<li style=\"display: block;float: left;width: 33%;\">")
                        .append("<label style=\"float: left;color: ")
                        .append(color).append(";\">").append(data.getName()).append("</label>")
                        .append("<span style=\"float: left; color: #808080;\">:</span>")
                        .append("<span style=\"float: left; color: #808080;\">")
                        .append(data.getData()).append("</span></li>");
            }
        }
        builder.append("</ul><br>");
        return builder.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        getMenuInflater().inflate(R.menu.menu_default, menu);
        mOptionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_copy_url).setVisible(true);
        menu.findItem(R.id.menu_open_by_browser).setVisible(true);

        menu.findItem(R.id.menu_search_post).setVisible(mProfileData != null);
        menu.findItem(R.id.menu_search_reply).setVisible(mProfileData != null);
        menu.findItem(R.id.menu_send_message).setVisible(mProfileData != null && !mCurrentUser);
        menu.findItem(R.id.menu_modify_avatar).setVisible(mProfileData != null && mCurrentUser);

        MenuItem item = menu.findItem(R.id.menu_ban_this_one);
        if (item != null) {

            if (mProfileData == null) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
                boolean ban = UserManagerImpl.getInstance().checkBlackList(mProfileData.getUid());
                item.setTitle(ban ? R.string.cancel_ban_thisone : R.string.ban_thisone);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send_message:
                sendShortMessage();
                break;
            case R.id.menu_search_post:
                searchPost();
                break;
            case R.id.menu_search_reply:
                searchReply();
                break;
            case R.id.menu_modify_avatar:
                startModifyAvatar();
                break;
            case R.id.menu_copy_url:
                FunctionUtils.copyToClipboard(this, getUrl());
                break;
            case R.id.menu_open_by_browser:
                FunctionUtils.openUrlByDefaultBrowser(this, getUrl());
                break;
            case R.id.menu_ban_this_one:
                banThisSB();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    public void banThisSB() {
        UserManager um = UserManagerImpl.getInstance();
        if (um.checkBlackList(mProfileData.getUid())) {
            um.removeFromBlackList(mProfileData.getUid());
            ToastUtils.success(R.string.remove_from_blacklist_success);
        } else {
            um.addToBlackList(mProfileData.getUserName(), mProfileData.getUid());
            ToastUtils.success(R.string.add_to_blacklist_success);
        }
    }

    private void startModifyAvatar() {
        Intent intent = new Intent();
        intent.putExtra("prefix", mProfileData.getSign());
        intent.setClass(this, AvatarPostActivity.class);
        startActivity(intent);
    }

    private void sendShortMessage() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                .withString("to", mProfileData.getUserName())
                .withString(ParamKey.KEY_ACTION, "new")
                .withString("messagemode", "yes")
                .navigation(this);
    }

    private void searchPost() {
        Intent intent = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
        intent.putExtra(ParamKey.KEY_AUTHOR_ID, Integer.parseInt(mProfileData.getUid()));
        intent.putExtra(ParamKey.KEY_AUTHOR, mProfileData.getUserName());
        startActivity(intent);
    }

    private void searchReply() {
        Intent intent = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
        intent.putExtra(ParamKey.KEY_AUTHOR_ID, Integer.parseInt(mProfileData.getUid()));
        intent.putExtra(ParamKey.KEY_SEARCH_POST, 1);
        intent.putExtra(ParamKey.KEY_AUTHOR, mProfileData.getUserName());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 321 && resultCode == Activity.RESULT_OK) {
            String signData = data.getStringExtra("sign");
            if (mProfileData != null) {
                mProfileData.setSign(signData);
                mSignWebView.requestLayout();
                handleSignWebView(mSignWebView, mProfileData);
            }
        } else if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            String avatarData = data.getStringExtra("avatar");
            mProfileData.setAvatarUrl(avatarData);
            mSignWebView.requestLayout();
            //  handleAvatar(avatarImage, mProfileData);
        }
    }

    private void handleSignWebView(WebViewEx contentTV, ProfileData ret) {
        ThemeManager theme = ThemeManager.getInstance();
        int bgColor, fgColor = getResources().getColor(theme.getForegroundColor());
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        contentTV.setLocalMode();
        contentTV.loadDataWithBaseURL(
                null,
                signatureToHtmlText(ret, fgColorStr, bgcolorStr),
                "text/html", "utf-8", null);
    }

    private void handleAdminWebView(WebViewEx contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
            fgColor = getResources().getColor(theme.getForegroundColor());
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
            fgColor = getResources().getColor(R.color.profilefcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        contentTV.setLocalMode();
        contentTV.loadDataWithBaseURL(
                null,
                adminToHtmlText(ret, fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    private void handleFameWebView(WebViewEx contentTV, ProfileData ret) {
        int bgColor, fgColor;
        ThemeManager theme = ThemeManager.getInstance();
        if (mThemeManager.isNightMode()) {
            bgColor = getResources().getColor(theme.getBackgroundColor(0));
            fgColor = getResources().getColor(theme.getForegroundColor());
        } else {
            bgColor = getResources().getColor(R.color.profilebgcolor);
            fgColor = getResources().getColor(R.color.profilefcolor);
        }
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        contentTV.setLocalMode();
        contentTV.loadDataWithBaseURL(
                null,
                fameToHtmlText(ret, fgColorStr, bgcolorStr), "text/html", "utf-8", null);
    }

    public String fameToHtmlText(final ProfileData ret, final String fgColorStr, final String bgcolorStr) {
        String color = "#121C46";
        if (mThemeManager.isNightMode()) {
            color = "#712D08";
        }
        String ngaHtml = createFameHtml(ret, color);
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public String adminToHtmlText(final ProfileData ret, final String fgColorStr, final String bgcolorStr) {
        String ngaHtml = createAdminHtml(ret);
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public String signatureToHtmlText(final ProfileData ret, final String fgColorStr, final String bgcolorStr) {
        String ngaHtml = ForumDecoder.decode(ret.getSign(), HtmlData.create(ret.getSign(), Utils.getNGAHost()));

        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr
                + "' size='2'>"
                + "<div style=\"border: 3px solid rgb(204, 204, 204);padding: 2px; \">"
                + ngaHtml + "</div>" + "</font></body>"
                + "<script type=\"text/javascript\" src=\"file:///android_asset/html/script.js\"></script>";

        return ngaHtml;
    }

    private void handleAvatar(ProfileData row) {
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getAvatarUrl());//
        ImageUtils.loadRoundCornerAvatar(mAvatarIv, avatarUrl);
        com.justwent.androidnga.bu.UserManager.INSTANCE.setAvatarUrl(row.getUid(), avatarUrl);
        ImageUtils.loadDefaultAvatar((ImageView) findViewById(R.id.iv_toolbar_layout_bg), avatarUrl);

    }

    @Override
    public void onError(String text) {
        ActivityUtils.showToast(text);
    }

    @Override
    public void onSuccess(ProfileData data) {
        mProfileData = data;
        if (data != null) {
            loadProfileInfo(data);
        }
    }
}
