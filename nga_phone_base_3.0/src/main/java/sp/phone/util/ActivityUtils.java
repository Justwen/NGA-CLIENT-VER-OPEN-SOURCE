package sp.phone.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.LauncherSubActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.ui.fragment.TopicHistoryFragment;
import sp.phone.ui.fragment.dialog.SearchDialogFragment;

public class ActivityUtils {

    public static final String dialogTag = "saying";
    static final String TAG = ActivityUtils.class.getSimpleName();
    static ActivityUtils instance;
    static Object lock = new Object();
    private DialogFragment df = null;

    public static final int REQUEST_CODE_LOGIN = 1;

    public static final int REQUEST_CODE_SETTING = 2;

    public static final int REQUEST_CODE_TOPIC_POST = 3;

    public static final int REQUEST_CODE_JUMP_PAGE = 4;

    public static final int REQUEST_CODE_SUB_BOARD = 4;

    private ActivityUtils() {
    }

    public static void showToast(Context context, int resId) {
        if (context != null) {
            ToastUtils.showToast(resId);
        }
    }

    public static void showToast(Context context, String res) {
        if (context != null) {
            ToastUtils.showToast(res);
        }
    }

    public static void showToast(String res) {
        showToast(ContextUtils.getContext(), res);
    }

    public static void showToast(int resId) {
        showToast(ContextUtils.getContext(), resId);
    }


    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;//instance;

    }

    static public String getSaying() {
        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            str = str.replace(";", "-----");
        }

        return str;

    }

    public void noticeSaying(Context context) {

        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    public void noticeSayingWithProgressBar(Context context) {

        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            noticebar("", str.replace(";", "-----"), context);
        } else {
            noticebar("", str, context);
        }
    }

    public void noticeSaying(String str, Context context) {

        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    public void noticeError(String error, Context context) {
        if (context != null) {
            HttpUtil.switchServer();
            notice(context.getString(R.string.error), error, context);
        }
    }

    private void notice(String title, String content, Context c) {

        if (c == null)
            return;
        NLog.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragment();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));

            }

        }

    }

    private void noticebar(String title, String content, Context c) {

        if (c == null)
            return;
        NLog.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragmentWithProgressBar();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));

            }

        }

    }

    public void noticebarsetprogress(int i) {
        NLog.d(TAG, "trying setprocess" + String.valueOf(i));
        if (df != null && df.getActivity() != null) {
            if (df instanceof SayingDialogFragmentWithProgressBar) {
                ((SayingDialogFragmentWithProgressBar) df).setProgress(i);
            }
        }
    }

    public void clear() {
        synchronized (lock) {
            this.df = null;
        }
    }

    public void dismiss() {

        synchronized (lock) {
            NLog.d(TAG, "trying dissmiss dialog");


            if (df != null && df.getActivity() != null) {
                NLog.d(TAG, "dissmiss dialog");

                try {
                    FragmentActivity fa = (FragmentActivity) (df.getActivity());
                    FragmentManager fm = fa.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    Fragment prev = fm.findFragmentByTag(dialogTag);
                    if (prev != null) {
                        ft.remove(prev);

                    }

                    ft.commit();
                } catch (Exception e) {
                    NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));
                }

                df = null;


            } else {
                df = null;
            }

        }
    }

    public static class SayingDialogFragmentWithProgressBar extends DialogFragment {

        ProgressDialog dialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (StringUtils.isEmpty(content))
                    content = ActivityUtils.getSaying();
                dialog.setMessage(content);
            }


            dialog.setCanceledOnTouchOutside(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(true);


            // etc...
            this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
            return dialog;
        }

        public void setProgress(int i) {
            if (dialog != null) {
                dialog.setProgress(i);
            }
        }

    }

    public static class SayingDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (StringUtils.isEmpty(content))
                    content = ActivityUtils.getSaying();
                dialog.setMessage(content);
            }


            dialog.setCanceledOnTouchOutside(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);


            // etc...
            this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
            return dialog;
        }


    }

    public static void startLoginActivity(Context context) {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_LOGIN).navigation((Activity) context, 1);
    }

    public static void startFavoriteTopicActivity(Context context) {
        if (!UserManagerImpl.getInstance().hasValidUser()) {
            startLoginActivity(context);
        } else {
            Intent intent = new Intent(context, PhoneConfiguration.getInstance().topicActivityClass);
            intent.putExtra("favor", 1);
            context.startActivity(intent);
        }
    }

    public static void startRecommendTopicActivity(Context context, Intent intent) {
        if (!UserManagerImpl.getInstance().hasValidUser()) {
            startLoginActivity(context);
        } else {
            intent.setClass(context, PhoneConfiguration.getInstance().topicActivityClass);
            context.startActivity(intent);
        }
    }

    public static void startTwentyFourActivity(Context context, Intent intent) {
        if (!UserManagerImpl.getInstance().hasValidUser()) {
            startLoginActivity(context);
        } else {
            intent.setClass(context, PhoneConfiguration.getInstance().topicActivityClass);
            context.startActivity(intent);
        }
    }

    public static void startSearchDialog(AppCompatActivity activity, Bundle bundle) {
        if (!UserManagerImpl.getInstance().hasValidUser()) {
            startLoginActivity(activity);
            return;
        }
        DialogFragment df = new SearchDialogFragment();
        df.setArguments(bundle);
        final String dialogTag = SearchDialogFragment.class.getSimpleName();
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            df.show(ft, dialogTag);
        } catch (Exception e) {

        }
    }

    public static void startHistoryTopicActivity(Context context) {
        Intent intent = new Intent(context, LauncherSubActivity.class);
        intent.putExtra("fragment", TopicHistoryFragment.class.getName());
        context.startActivity(intent);
    }
}
