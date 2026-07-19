package com.justwen.androidnga.cloud;

import android.content.Context;

import java.util.Map;

public interface ICloudSever {

    void init(Context context);

    void pingBack(Context context, String event);

    void pingBack(Context context, String event, Map<String, String> map);
}
