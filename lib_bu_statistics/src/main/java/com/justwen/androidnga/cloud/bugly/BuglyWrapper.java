package com.justwen.androidnga.cloud.bugly;

import android.content.Context;

import com.justwen.androidnga.cloud.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

public class BuglyWrapper {

    public static void init(Context context) {
        if (!BuildConfig.DEBUG) {
            int id = context.getResources().getIdentifier("bugly_app_id", "string", context.getPackageName());
            if (id > 0) {
                CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
                strategy.setAppChannel("GooglePlay");
                CrashReport.initCrashReport(context, context.getString(id), false, strategy);
            }
        }
    }
}