package gov.anzong.androidnga.arouter;

/**
 * Created by Justwen on 2018/4/20.
 */
public class ARouterConstants {

    public static final String ACTIVITY_PROFILE = "/activity/profile";

    public static final String ACTIVITY_LOGIN = com.justwen.androidnga.base.activity.ARouterConstants.ACTIVITY_LOGIN;

    public static final String ACTIVITY_POST = "/activity/post";

    public static final String ACTIVITY_MESSAGE_POST = com.justwen.androidnga.base.activity.ARouterConstants.ACTIVITY_MESSAGE_POST;

    public static final String ACTIVITY_TOPIC_CONTENT = "/activity/topic_content";

    public static final String ACTIVITY_TOPIC_LIST = "/activity/topic_list";

    public static final String ACTIVITY_SEARCH = "/activity/search";

    public static final String ACTIVITY_NOTIFICATION = "/activity/notification";

    public static final String ACTIVITY_MESSAGE_LIST = com.justwen.androidnga.base.activity.ARouterConstants.ACTIVITY_MESSAGE_LIST;

    public static final String[] ACTIVITY_NEED_LOGIN = {
            ACTIVITY_MESSAGE_LIST,
            ACTIVITY_PROFILE,
            ACTIVITY_NOTIFICATION,
            ACTIVITY_SEARCH,
            ACTIVITY_TOPIC_LIST,
    };
}
