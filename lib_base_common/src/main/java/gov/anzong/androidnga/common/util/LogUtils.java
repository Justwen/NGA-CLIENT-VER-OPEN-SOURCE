package gov.anzong.androidnga.common.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.common.BuildConfig;

/**
 * 带开关的Log
 * Created by elrond on 2017/9/4.
 */

public class LogUtils {

    private static boolean sDebugMode = BuildConfig.DEBUG;

    public static final String TAG = "NGAClient";

    private static ThreadLocal<Map<String, Long>> sComputeCostMap = new ThreadLocal<>();

    public static void setDebug(boolean debug) {
        sDebugMode = debug;
    }

    public static int v(String tag, String msg) {
        if (sDebugMode) {
            return Log.v(tag, msg);
        } else {
            return 0;
        }
    }

    public static int d(String tag, String msg) {
        if (sDebugMode) {
            return Log.d(tag, msg);
        } else {
            return 0;
        }
    }

    public static int i(String tag, String msg) {
        if (sDebugMode) {
            return Log.i(tag, msg);
        } else {
            return 0;
        }
    }

    public static int w(String tag, String msg) {
        if (sDebugMode) {
            return Log.w(tag, msg);
        } else {
            return 0;
        }
    }

    public static int e(String tag, String msg) {
        return Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (msg != null) {
            Log.e(TAG, msg);
        }
    }


    public static int e(String tag, String msg, Throwable throwable) {
        if (sDebugMode) {
            return Log.e(tag, msg, throwable);
        } else {
            return 0;
        }
    }

    public static String getStackTraceString(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }


    // 可以打印行数
    public static void d(Object msg) {
        if (sDebugMode) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (elements.length < 3) {
                Log.e(TAG, "Stack to shallow");
            } else {
                try {
                    String fullClassName = elements[3].getClassName();
                    String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                    String methodName = elements[3].getMethodName();
                    int lineNumber = elements[3].getLineNumber();
                    Log.d(TAG, className + "." + methodName + "():"
                            + lineNumber + " " + msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void print(Throwable throwable) {
        if (sDebugMode) {
            String msg = Log.getStackTraceString(throwable);
            Log.w(TAG, msg);
        }
    }

    public static void d() {
        if (sDebugMode) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (elements.length < 3) {
                Log.e(TAG, "Stack to shallow");
            } else {
                try {
                    String fullClassName = elements[3].getClassName();
                    String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                    String methodName = elements[3].getMethodName();
                    int lineNumber = elements[3].getLineNumber();
                    Log.d(TAG, className + "." + methodName + "():"
                            + lineNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void computeCost(String tag) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Map<String, Long> computeMap = sComputeCostMap.get();
        if (computeMap == null) {
            computeMap = new HashMap<>();
            sComputeCostMap.set(computeMap);
        }
        if (computeMap.containsKey(tag)) {
            long prevTime = computeMap.get(tag);
            computeMap.remove(tag);
            Log.d(TAG, tag + " cost " + (System.currentTimeMillis() - prevTime) + "ms" + " " + Thread.currentThread().getName());
        } else {
            long current = System.currentTimeMillis();
            computeMap.put(tag, current);
        }
    }

}
