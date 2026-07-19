package gov.anzong.androidnga.base.logger;

public class Logger {

    private static ILogger sLoggerDelegate = new ReleaseLogger();

    private static boolean sBuildDebugMode;


    public static void setBuildDebugMode(boolean buildDebugMode) {
        sBuildDebugMode = buildDebugMode;
        initLogger();
    }

    public static void setLogger(ILogger logger) {
        if (sLoggerDelegate != null) {
            sLoggerDelegate.close();
        }
        if (logger == null) {
            sLoggerDelegate = new DebugLogger();
        } else {
            sLoggerDelegate = logger;
        }
    }

    private static void initLogger() {
        if (sBuildDebugMode) {
            sLoggerDelegate = new DebugLogger();
        } else {
            sLoggerDelegate = new ReleaseLogger();
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        sLoggerDelegate.d(tag, msg, t);
    }

    public static void d(String tag, String msg) {
        sLoggerDelegate.d(tag, msg);
    }

    public static void d(String msg) {
        sLoggerDelegate.d(msg);
    }

    public static void d(int msg) {
        sLoggerDelegate.d(msg);
    }

    public void d(float msg) {
        sLoggerDelegate.d(msg);
    }

    public static void d(boolean msg) {
        sLoggerDelegate.d(msg);
    }

    public static void d(Throwable throwable) {
        sLoggerDelegate.d(throwable);
    }

    public static void d() {
        sLoggerDelegate.d();
    }

}
