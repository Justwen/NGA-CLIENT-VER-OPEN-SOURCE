package gov.anzong.androidnga.base.logger;

import android.util.Log;

public interface ILogger {

    String TAG = "NGAClient";

    default void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    default void d(String tag, String msg, Throwable throwable) {
        Log.d(tag, msg, throwable);
    }

    default void close() {

    }

    default void d(int msg) {
        d(TAG, String.valueOf(msg));
    }

    default void d(float msg) {
        d(TAG, String.valueOf(msg));
    }

    default void d(boolean msg) {
        d(TAG, String.valueOf(msg));
    }

    default void d(String msg) {
        d(TAG, msg);
    }

    default void d(Throwable throwable) {
        d(TAG, "", throwable);
    }

    default void d() {
    }

}
