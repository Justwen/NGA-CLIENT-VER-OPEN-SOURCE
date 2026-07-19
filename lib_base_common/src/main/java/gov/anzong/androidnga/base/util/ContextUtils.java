package gov.anzong.androidnga.base.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class ContextUtils {

    private static Application sApplication;

    private static Activity sResumedActivity;

    public static Context getContext() {
        return sResumedActivity == null ? sApplication : sResumedActivity;
    }

    public static void setApplication(Application application) {
        sApplication = application;
        sApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                sResumedActivity = activity;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                sResumedActivity = activity;

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (activity == sResumedActivity) {
                    sResumedActivity = null;
                }

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity == sResumedActivity) {
                    sResumedActivity = null;
                }

            }
        });
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    @ColorInt
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public static String getString(@StringRes int id) {
        return getContext().getString(id);
    }

    public static int getDimension(@DimenRes int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
