package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.activity.ProfileActivity;
import gov.anzong.androidnga.activity.SignPostActivity;
import gov.anzong.androidnga.activity.TopicListActivity;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;

public class PhoneConfiguration extends PreferenceKey implements SharedPreferences.OnSharedPreferenceChangeListener {
    public Class<?> topicActivityClass = TopicListActivity.class;
    public Class<?> articleActivityClass = ArticleListActivity.class;
    public Class<?> postActivityClass = PostActivity.class;
    public Class<?> signPostActivityClass = SignPostActivity.class;
    public Class<?> profileActivityClass = ProfileActivity.class;

    private boolean mNotificationEnabled;

    private boolean mNotificationSoundEnabled;

    private boolean mShowSignature;

    private boolean mShowColorText;

    private boolean mUpdateAfterPost;

    private boolean mShowClassicIcon;

    private boolean mLeftHandMode;

    private boolean mShowBottomTab;

    private boolean mFilterSubBoard;

    private boolean mSortByPostOrder;

    private String mAvatarLoadStrategy = PreferenceKey.IMAGE_LOAD_ALWAYS;

    private String mImageLoadStrategy = PreferenceKey.IMAGE_LOAD_ALWAYS;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        switch (key) {
            case PreferenceKey.NOTIFIACTION_SOUND:
                mNotificationSoundEnabled = sp.getBoolean(key, true);
                break;
            case PreferenceKey.ENABLE_NOTIFIACTION:
                mNotificationEnabled = sp.getBoolean(key, true);
                break;
            case PreferenceKey.SHOW_SIGNATURE:
                mShowSignature = sp.getBoolean(key, false);
                break;
            case PreferenceKey.SHOW_COLORTXT:
                mShowColorText = sp.getBoolean(key, false);
                break;
            case PreferenceKey.REFRESH_AFTERPOST_SETTING_MODE:
                mUpdateAfterPost = sp.getBoolean(key, true);
                break;
            case PreferenceKey.SHOW_ICON_MODE:
                mShowClassicIcon = sp.getBoolean(key, false);
                break;
            case PreferenceKey.LEFT_HAND:
                mLeftHandMode = sp.getBoolean(key, false);
                break;
            case PreferenceKey.BOTTOM_TAB:
                mShowBottomTab = sp.getBoolean(key, false);
                break;
            case PreferenceKey.FILTER_SUB_BOARD:
                mFilterSubBoard = sp.getBoolean(key, false);
                break;
            case PreferenceKey.SORT_BY_POST:
                mSortByPostOrder = sp.getBoolean(key, false);
                break;
            case PreferenceKey.KEY_LOAD_AVATAR_STRATEGY:
                mAvatarLoadStrategy = sp.getString(key, mAvatarLoadStrategy);
                break;
            case PreferenceKey.KEY_LOAD_IMAGE_STRATEGY:
                mImageLoadStrategy = sp.getString(key, mImageLoadStrategy);
                break;
            default:
                break;
        }

    }

    private static class PhoneConfigurationHolder {

        private static PhoneConfiguration sInstance = new PhoneConfiguration();
    }

    public static PhoneConfiguration getInstance() {
        return PhoneConfigurationHolder.sInstance;
    }


    private PhoneConfiguration() {
        initialize();
    }

    private void initialize() {
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        mNotificationSoundEnabled = sp.getBoolean(PreferenceKey.NOTIFIACTION_SOUND, true);
        mNotificationEnabled = sp.getBoolean(PreferenceKey.ENABLE_NOTIFIACTION, true);
        mShowSignature = sp.getBoolean(PreferenceKey.SHOW_SIGNATURE, false);
        mShowColorText = sp.getBoolean(PreferenceKey.SHOW_COLORTXT, false);
        mUpdateAfterPost = sp.getBoolean(PreferenceKey.REFRESH_AFTERPOST_SETTING_MODE, true);
        mShowClassicIcon = sp.getBoolean(PreferenceKey.SHOW_ICON_MODE, false);
        mLeftHandMode = sp.getBoolean(PreferenceKey.LEFT_HAND, false);
        mShowBottomTab = sp.getBoolean(PreferenceKey.BOTTOM_TAB, false);
        mFilterSubBoard = sp.getBoolean(PreferenceKey.FILTER_SUB_BOARD, false);
        mSortByPostOrder = sp.getBoolean(PreferenceKey.SORT_BY_POST, false);

        mImageLoadStrategy = sp.getString(PreferenceKey.KEY_LOAD_IMAGE_STRATEGY, mImageLoadStrategy);
        mAvatarLoadStrategy = sp.getString(PreferenceKey.KEY_LOAD_AVATAR_STRATEGY, mAvatarLoadStrategy);
    }

    public boolean needSortByPostOrder() {
        return mSortByPostOrder;
    }

    public boolean isShowBottomTab() {
        return mShowBottomTab;
    }

    public boolean needFilterSubBoard() {
        return mFilterSubBoard;
    }

    public boolean isLeftHandMode() {
        return mLeftHandMode;
    }

    public boolean needUpdateAfterPost() {
        return mUpdateAfterPost;
    }

    public boolean isShowClassicIcon() {
        return mShowClassicIcon;
    }

    public int getAvatarSize() {
        try {
            return PreferenceUtils.getData(PreferenceKey.KEY_AVATAR_SIZE, Constants.AVATAR_SIZE_DEFAULT);
        } catch (Exception e) {
            setAvatarSize(Constants.AVATAR_SIZE_DEFAULT);
            return Constants.AVATAR_SIZE_DEFAULT;
        }
    }

    public void setAvatarSize(int value) {
        PreferenceUtils.putData(PreferenceKey.KEY_AVATAR_SIZE, value);
    }

    public int getEmoticonSize() {
        return PreferenceUtils.getData(PreferenceKey.KEY_EMOTICON_SIZE, Constants.EMOTICON_SIZE_DEFAULT);
    }

    public void setEmoticonSize(int value) {
        PreferenceUtils.putData(PreferenceKey.KEY_EMOTICON_SIZE, value);
    }

    public float getTopicTitleSize() {
        return PreferenceUtils.getData(PreferenceKey.KEY_TOPIC_TITLE_SIZE, Constants.TOPIC_TITLE_SIZE_DEFAULT);
    }

    public void setTopicTitleSize(int size) {
        PreferenceUtils.putData(PreferenceKey.KEY_TOPIC_TITLE_SIZE, size);
    }

    public int getTopicContentSize() {
        return PreferenceUtils.getData(PreferenceKey.KEY_TOPIC_CONTENT_SIZE, Constants.TOPIC_CONTENT_SIZE_DEFAULT);
    }

    public void setTopicContentSize(int size) {
        PreferenceUtils.putData(PreferenceKey.KEY_TOPIC_CONTENT_SIZE, size);
    }

    public int getWebViewTextZoom() {
        return PreferenceUtils.getData(PreferenceKey.KEY_WEBVIEW_TEXT_ZOOM, Constants.WEBVIEW_DEFAULT_TEXT_ZOOM);
    }

    public void setWebViewTextZoom(int textRoom) {
        PreferenceUtils.putData(PreferenceKey.KEY_WEBVIEW_TEXT_ZOOM, textRoom);
    }

    public boolean useSolidColorBackground() {
        return ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE).getBoolean(PreferenceKey.KEY_USE_SOLID_COLOR_BG, true);
    }

    public boolean isShowSignature() {
        return mShowSignature;
    }

    public boolean isShowColorText() {
        return mShowColorText;
    }

    public boolean isNotificationEnabled() {
        return mNotificationEnabled;
    }

    public boolean isNotificationSoundEnabled() {
        return mNotificationSoundEnabled;
    }

    public boolean isAvatarLoadEnabled(boolean defValue) {
        if (mAvatarLoadStrategy.equals(PreferenceKey.IMAGE_LOAD_ALWAYS)) {
            return true;
        } else if (mAvatarLoadStrategy.equals(PreferenceKey.IMAGE_LOAD_NEVER)) {
            return false;
        } else {
            return defValue;
        }
    }

    public boolean isImageLoadEnabled() {
        if (mImageLoadStrategy.equals(PreferenceKey.IMAGE_LOAD_ALWAYS)) {
            return true;
        } else if (mImageLoadStrategy.equals(PreferenceKey.IMAGE_LOAD_NEVER)) {
            return false;
        } else {
            return DeviceUtils.isWifiConnected(ContextUtils.getContext());
        }
    }

    @Deprecated
    public int getWebSize() {
        return getTopicContentSize();
    }

    public String getCookie() {
        return UserManagerImpl.getInstance().getCookie();
    }

}

