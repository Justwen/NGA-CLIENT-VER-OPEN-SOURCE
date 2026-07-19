package gov.anzong.androidnga;

import android.app.Application;
import android.os.Process;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.cloud.CloudServerManager;
import com.justwent.androidnga.bu.UserManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.common.util.NLog;
import gov.anzong.androidnga.common.util.ReflectUtils;
import gov.anzong.androidnga.db.AppDatabase;
import sp.phone.common.UserManagerImpl;
import sp.phone.common.VersionUpgradeHelper;
import sp.phone.task.CheckInTask;

public class NgaClientApp extends Application {

    private static final String TAG = NgaClientApp.class.getSimpleName();

    private static boolean sNewVersion;

    @Override
    public void onCreate() {
        ContextUtils.setApplication(this);
        initLogger();
        PreferenceUtils.transfer(this);
        checkNewVersion();
        VersionUpgradeHelper.upgrade();
        AppDatabase.init(this);
        initCoreModule();
        initRouter();
        checkIn();
        super.onCreate();

        // fixWebViewMultiProcessException();
        CloudServerManager.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandlerProxy(Thread.getDefaultUncaughtExceptionHandler()));
    }

    private void initLogger() {
        Logger.setBuildDebugMode(BuildConfig.DEBUG);
        Logger.d(TAG, "app nga android start");
        NLog.setDebug(BuildConfig.DEBUG);
    }

    private void fixWebViewMultiProcessException() {
        try {
            File dataDir = getDataDir();
            File[] dirs = dataDir.listFiles();

            Object ppidObj = ReflectUtils.invokeMethod(Process.class, "myPpid");

            int ppid = ppidObj != null ? (int) ppidObj : Process.myPid();

            if (dirs != null) {
                for (File dir : dirs) {
                    if (dir.getName().contains("webview")) {
                        if (!dir.getName().contains("webview_" + ppid)) {
                            ThreadUtils.postOnSubThread(() -> {
                                try {
                                    FileUtils.deleteDirectory(dir);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                        }
                    }
                }
            }

            WebView.setDataDirectorySuffix(String.valueOf(ppid));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private void initRouter() {
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    private void initCoreModule() {
        UserManagerImpl.getInstance().initialize(this);
        UserManager.INSTANCE.getActiveUser();
        RetrofitHelper.setCookieProvider(() -> UserManagerImpl.getInstance().getCookie());
//        // 注册crashHandler
//        CrashHandler.getInstance().init(this);
        initCoreModuleAsync();

    }

    private void initCoreModuleAsync() {
        ThreadUtils.postOnSubThread(new Runnable() {
            @Override
            public void run() {
                ForumBoardViewModel.INSTANCE.getBoardLiveData();
            }
        });
    }

    private void checkNewVersion() {
        int versionCode = PreferenceUtils.getData(PreferenceKey.VERSION_CODE, 0);
        if (BuildConfig.VERSION_CODE > versionCode) {
            PreferenceUtils.putData(PreferenceKey.PREVIOUS_VERSION_CODE, versionCode);
            PreferenceUtils.putData(PreferenceKey.VERSION_CODE, BuildConfig.VERSION_CODE);
            sNewVersion = true;
            PreferenceUtils.putData(PreferenceKey.KEY_WEBVIEW_DATA_INDEX, 0);
        }
    }

    private void checkIn() {
        CheckInTask.autoCheckIn(this);
    }

    public static boolean isNewVersion() {
        return sNewVersion;
    }

    public static void setNewVersion(boolean newVersion) {
        sNewVersion = newVersion;
    }

}
