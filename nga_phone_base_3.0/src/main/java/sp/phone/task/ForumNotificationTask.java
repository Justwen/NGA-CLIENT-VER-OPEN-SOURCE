package sp.phone.task;

import androidx.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.mvp.model.convert.ForumNotificationFactory;
import sp.phone.mvp.model.entity.NotificationInfo;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.rxjava.BaseSubscriber;
import gov.anzong.androidnga.common.util.NLog;

public class ForumNotificationTask {

    private RetrofitService mService;

    private LifecycleProvider<FragmentEvent> mLifecycleProvider;

    private String mNotificationUrl =  Utils.getNGAHost() + "nuke.php?__lib=noti&__output=8&__act=get_all";

    public ForumNotificationTask(LifecycleProvider<FragmentEvent> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
        mService = RetrofitHelper.getInstance().getService();
    }


    // 只返回最近被喷的信息
    public void queryRecentReply(@NonNull OnHttpCallBack<List<RecentReplyInfo>> callBack) {
        mService.get(mNotificationUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, List<RecentReplyInfo>>() {
                    @Override
                    public List<RecentReplyInfo> apply(String s) throws Exception {
                        return ForumNotificationFactory.buildRecentReplyList(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<List<RecentReplyInfo>>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<List<RecentReplyInfo>>() {
                    @Override
                    public void onNext(List<RecentReplyInfo> s) {
                        callBack.onSuccess(s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

    // 返回最近被喷和短信的信息
    public void queryNotification(@NonNull OnHttpCallBack<List<NotificationInfo>> callBack) {

        mService.get(mNotificationUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, List<NotificationInfo>>() {
                    @Override
                    public List<NotificationInfo> apply(String s) throws Exception {
                        return ForumNotificationFactory.buildNotificationList(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<NotificationInfo>>() {
                    @Override
                    public void onNext(List<NotificationInfo> s) {
                        callBack.onSuccess(s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

    public void clearAllNotification() {
        String NGA_NOTIFICATION_DELETE_ALL = Utils.getNGAHost()+ "nuke.php?__lib=noti&raw=3&__act=del";

        mService.post(NGA_NOTIFICATION_DELETE_ALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        NLog.d(s);
                    }
                });

    }
}
