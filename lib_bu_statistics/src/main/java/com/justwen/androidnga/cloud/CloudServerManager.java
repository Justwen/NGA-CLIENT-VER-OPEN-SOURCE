package com.justwen.androidnga.cloud;

import android.content.Context;

import com.justwen.androidnga.cloud.bugly.BuglyWrapper;
import com.justwen.androidnga.cloud.umeng.UMengWrapper;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

/**
 * @author yangyihang
 */
public class CloudServerManager {

    private static ICloudSever sCloudServer;

    public static void init(Context context) {
        try {
            BuglyWrapper.init(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            sCloudServer = new UMengWrapper();
            sCloudServer.init(context);
        } catch (Throwable e) {
            e.printStackTrace();
            sCloudServer = null;
        }

    }

    public static void putCrashData(Context context, String key, String value) {
        CrashReport.putUserData(context, key, value);
    }

    public static void pingBack(Context context, String event) {
        if (sCloudServer != null) {
            sCloudServer.pingBack(context, event);
        }
    }

    public static void pingBack(Context context, String event, Map<String, String> map) {
        if (sCloudServer != null) {
            sCloudServer.pingBack(context, event, map);
        }
    }

    public static void checkUpgrade() {

    }
}
