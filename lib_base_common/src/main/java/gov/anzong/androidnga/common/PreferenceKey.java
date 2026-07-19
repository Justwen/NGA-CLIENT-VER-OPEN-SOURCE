package gov.anzong.androidnga.common;

import android.preference.PreferenceManager;

import gov.anzong.androidnga.base.util.ContextUtils;

public class PreferenceKey {

    public static final String PREFERENCE_AVATAR = "avatar";

    public static final String PREFERENCE_SETTINGS = "perference";

    @Deprecated
    public static final String PERFERENCE = PreferenceManager.getDefaultSharedPreferencesName(ContextUtils.getContext());

    String DOWNLOAD_IMG_QUALITY_NO_WIFI = "download_img_quality_without_wifi";
    public static final String ENABLE_NOTIFIACTION = "enableNotification";
    public static final String NOTIFIACTION_SOUND = "notificationSound";
    public static final String NIGHT_MODE = "nightmode";
    String VERSION = "version";
    public static final String REFRESH_AFTERPOST_SETTING_MODE = "refresh_after_post_setting_mode";
    public static final String SHOW_SIGNATURE = "showSignature";
    String SHOW_STATIC = "showStatic";
    public static final String SHOW_COLORTXT = "showColortxt";
    String SHOW_NEWWEIBA = "showNewweiba";
    String SHOW_LAJIBANKUAI = "showLajibankuai";
    String HANDSIDE = "HandSide";

    public static final String USER_LIST = "userList";
    public static final String SHOW_ICON_MODE = "showiconmode";

    public static final String ADJUST_SIZE = "adjust_size";

    public static final String MATERIAL_THEME = "material_theme";

    public static final String BOTTOM_TAB = "bottom_tab";

    public static final String LEFT_HAND = "left_hand";

    public static final String BOOKMARK_BOARD = "bookmark_board";

    public static final String CHECK_IN_LAST_TIME = "check_in_last_time";

    public static final String USER_ACTIVE_INDEX = "user_active_index";

    public static final String SORT_BY_POST = "sort_by_post";

    public static final String PREF_USER = "pref_user";

    public static final String FILTER_SUB_BOARD = "filter_sub_board";

    public static final String KEY_TOPIC_HISTORY = "topic_history";

    public static final String KEY_REPLY_COUNT = "reply_count";

    public static final String KEY_NGA_DOMAIN = "nga_domain";

    public static final String KEY_SEARCH_HISTORY_TOPIC = "search_history_topic";

    public static final String KEY_SEARCH_HISTORY_BOARD = "search_history_board";

    public static final String KEY_SEARCH_HISTORY_USER = "search_history_user";

    public static final String KEY_TOPIC_TITLE_SIZE = "topic_title_size";

    public static final String KEY_TOPIC_CONTENT_SIZE = "topic_content_size";

    public static final String KEY_AVATAR_SIZE = "avatar_size";

    public static final String KEY_EMOTICON_SIZE = "emoticon_size";

    public static final String KEY_USE_SOLID_COLOR_BG = "use_solid_color_bg";

    public static final String KEY_CHECK_UPGRADE_STATE = "key_check_upgrade_state";

    public static final String KEY_CHECK_UPGRADE_TIME = "key_check_upgrade_time";

    public static final String KEY_NIGHT_MODE_FOLLOW_SYSTEM = "key_night_mode_follow_system";

    public static final String VERSION_MAJOR_CODE = "version_major_code";

    public static final String VERSION_MIRROR_CODE = "version_mirror_code";

    public static final String VERSION_CODE = "version_code";

    public static final String PREVIOUS_VERSION_CODE = "previous_version_code";

    public static final String KEY_WEBVIEW_DATA_INDEX = "webview_data_index";

    public static final String KEY_CLEAR_CACHE = "key_clear_cache";

    public static final String KEY_WEBVIEW_TEXT_ZOOM = "key_webview_zoom_size";

    public static final String KEY_PRELOAD_BOARD_VERSION = "key_preload_board_version";

    public static final String KEY_LOAD_AVATAR_STRATEGY = "pref_load_avatar_strategy";

    public static final String KEY_LOAD_IMAGE_STRATEGY = "pref_load_pic_strategy";

    public static final String IMAGE_LOAD_ALWAYS = "0";

    public static final String IMAGE_LOAD_NEVER = "1";

    public static final String IMAGE_LOAD_ONLY_WIFI = "2";

    @Deprecated
    public static final String DOWNLOAD_AVATAR_NO_WIFI = "download_avatar_nowifi";

    @Deprecated
    public static final String DOWNLOAD_IMG_NO_WIFI = "down_load_without_wifi";

    public static final String USER_AGENT = "preference_key_ua";

}
