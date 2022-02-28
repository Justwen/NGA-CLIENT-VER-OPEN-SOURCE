package com.justwen.androidnga.cloud.umeng;

import android.content.Context;

import com.justwen.androidnga.cloud.BuildConfig;
import com.justwen.androidnga.cloud.ICloudSever;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.commonsdk.UMConfigure;

import java.util.Map;

public class UMengWrapper implements ICloudSever {

    @Override
    public void init(Context context) {
//        int appResId = context.getResources().getIdentifier("umeng_app_key", "string", context.getPackageName());
//        if (appResId > 0) {
//            UMConfigure.setLogEnabled(BuildConfig.DEBUG);
//            UMConfigure.init(context, context.getString(appResId), "GooglePlay", UMConfigure.DEVICE_TYPE_PHONE, null);
//            MobclickAgent.setCatchUncaughtExceptions(false);
//            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
//        }
    }

    @Override
    public void pingBack(Context context, String event) {
//        if (!UMConfigure.getInitStatus()) {
//            return;
//        }
//        MobclickAgent.onEvent(context, event);
    }

    @Override
    public void pingBack(Context context, String event, Map<String, String> map) {
//        if (!UMConfigure.getInitStatus()) {
//            return;
//        }
//        MobclickAgent.onEvent(context, event, map);
    }
}
