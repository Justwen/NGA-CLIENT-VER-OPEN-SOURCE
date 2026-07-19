package sp.phone.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.justwen.androidnga.base.activity.ARouterConstants;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.RecentNotificationActivity;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.mvp.model.entity.NotificationInfo;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.task.ForumNotificationTask;

public class NotificationController {

    private static final String NOTIFICATION_ID = "NGA";

    private static final String NOTIFICATION_NAME = "NGA_CLIENT";

    private ForumNotificationTask mNotificationTask;

    private PhoneConfiguration mConfiguration;

    private static final int DELAY_TIME = 30 * 1000;

    private long mLastQueryTime;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            mNotificationTask.queryNotification(new OnHttpCallBack<List<NotificationInfo>>() {
                @Override
                public void onError(String text) {

                }

                @Override
                public void onSuccess(List<NotificationInfo> data) {
                    showNotification(data);
                }
            });
            return false;
        }
    });

    private static class SingleTonHolder {
        static NotificationController sInstance = new NotificationController();
    }

    public static NotificationController getInstance() {
        return SingleTonHolder.sInstance;
    }

    private NotificationController() {
        mNotificationTask = new ForumNotificationTask(null);
        mConfiguration = PhoneConfiguration.getInstance();
        createNotificationChannel(ContextUtils.getContext());
    }

    public void checkNotificationDelay() {
        if (mConfiguration.isNotificationEnabled()
                && UserManagerImpl.getInstance().hasValidUser()
                && (System.currentTimeMillis() - mLastQueryTime) > DELAY_TIME) {
            mLastQueryTime = System.currentTimeMillis();
            mHandler.sendEmptyMessage(0);
        }

    }

    private void showNotification(List<NotificationInfo> infoList) {
        if (infoList.isEmpty() || !infoList.get(0).isUnread()) {
            return;
        }
        boolean hasMessage = false;
        ArrayList<RecentReplyInfo> recentReplyList = new ArrayList<>();
        for (NotificationInfo info : infoList) {
            if (info instanceof RecentReplyInfo) {
                recentReplyList.add((RecentReplyInfo) info);
            } else {
                hasMessage = true;
            }
        }

        if (hasMessage) {
            showMessageNotification();
        }

        if (!recentReplyList.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(ContextUtils.getContext())
                    .edit().putInt(PreferenceKey.KEY_REPLY_COUNT, recentReplyList.size()).apply();
            showReplyNotification(recentReplyList);
        }
    }

    private void showReplyNotification(ArrayList<RecentReplyInfo> infoList) {

        int id = Integer.parseInt(infoList.get(0).getPidStr());
        Context context = ContextUtils.getContext();

        Intent intent = new Intent(context, RecentNotificationActivity.class);

        intent.putParcelableArrayListExtra("unread", infoList);

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = buildNotification(context);
        builder.setContentIntent(pending)
                .setTicker("有新的被喷提醒，请查看")
                .setContentTitle("有新的被喷提醒，请查看")
                .setContentText("有新的被喷提醒，请查看");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(id, builder.build()); // 通过通知管理器发送通知
        }
    }

    private void showMessageNotification() {
        Context context = ContextUtils.getContext();

        Intent intent = new Intent();
        intent.setClassName(context, ARouterConstants.ACTIVITY_MESSAGE_LIST_NAME);

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = buildNotification(context);
        builder.setContentIntent(pending)
                .setTicker("有新的短消息，请查看")
                .setContentTitle("有新的短消息，请查看")
                .setContentText("有新的短消息，请查看");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(1000, builder.build()); // 通过通知管理器发送通知
        }
    }

    private NotificationCompat.Builder buildNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_ID);
        builder.setLights(Color.parseColor("#fff0cd"), 2333, 0)
                .setSmallIcon(R.drawable.nga_bg) //设置图标
                .setWhen(System.currentTimeMillis()) //发送时间
                .setDefaults(mConfiguration.isNotificationSoundEnabled() ? Notification.DEFAULT_ALL : Notification.DEFAULT_LIGHTS) //设置默认的提示音，振动方式，灯光
                //打开程序后图标消失
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground);
        return builder;
    }

    private void createNotificationChannel(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }
        String id = NOTIFICATION_ID;
        CharSequence name = NOTIFICATION_NAME;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(true); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.GREEN); //小红点颜色
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        notificationManager.createNotificationChannel(channel);
    }
}
