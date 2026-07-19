package gov.anzong.androidnga.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.common.R;

/**
 * Created by Justwen on 2018/7/2.
 */
public class ForumUtils {

    public static String getAvailableDomain() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain)[index];
    }

    public static String getAvailableDomainNoHttp() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain_no_http)[index];
    }

    /**
     * @param statusCode
     * @return 返回子板块是否被订阅
     */
    public static boolean isBoardSubscribed(int statusCode) {
        // 3,810 返回false
        return statusCode == 7 || statusCode == 558 || statusCode == 542 || statusCode == 2606 || statusCode == 2590
                || statusCode == 4654;
    }

}
