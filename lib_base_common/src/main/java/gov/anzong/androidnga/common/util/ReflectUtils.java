package gov.anzong.androidnga.common.util;

import java.lang.reflect.Method;

public class ReflectUtils {

    public static Object invokeMethod(Class<?> clz, Object obj, String methodName, Class<?>[] parameterTypes, Object[] params) {
        try {
            Method method = clz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethod(Class<?> clz, Object obj, String methodName) {
        return invokeMethod(clz, obj, methodName, null, null);
    }

    public static Object invokeMethod(Class<?> clz, String methodName) {
        return invokeMethod(clz, null, methodName, null, null);
    }
}
