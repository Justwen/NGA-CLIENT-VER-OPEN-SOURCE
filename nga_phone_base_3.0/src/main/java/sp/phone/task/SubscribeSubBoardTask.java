package sp.phone.task;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.mvp.model.entity.SubBoard;
import sp.phone.rxjava.BaseSubscriber;

/**
 * Created by Justwen on 2018/1/27.
 */

public class SubscribeSubBoardTask {

    private RetrofitService mService;


    private LifecycleProvider<FragmentEvent> mLifecycleProvider;

    public SubscribeSubBoardTask(LifecycleProvider<FragmentEvent> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    public void subscribe(SubBoard subBoard, final OnHttpCallBack<String> callBack) {
        mService.post(getUrl(subBoard, true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<String>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<String>() {

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }


                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("成功")) {
                            callBack.onSuccess("取消屏蔽成功，请刷新界面！");
                        } else {
                            callBack.onError(s);
                        }
                        super.onNext(s);
                    }
                });

    }

    public void unsubscribe(SubBoard subBoard, final OnHttpCallBack<String> callBack) {
        mService.post(getUrl(subBoard, false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<String>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<String>() {

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("成功")) {
                            callBack.onSuccess("屏蔽子板块成功，请刷新界面！");
                        } else {
                            callBack.onError(s);
                        }
                    }
                });

    }

    private String getUrl(SubBoard subBoard, boolean isSubscribe) {
        int type = subBoard.getType();
        String action = getAction(type, isSubscribe);
        String parentFid = subBoard.getParentFidStr();
        String fid = type == 1 ? subBoard.getTidStr() : String.valueOf(subBoard.getFid());
        String url;
        if (!isSubscribe) {
            url = String.format("http://bbs.ngacn.cc/nuke.php?__lib=user_option&__act=set&raw=3&type=%s&__output=8&fid=%s&%s=%s", type, parentFid, action, fid);
        } else {
            url = String.format("http://bbs.ngacn.cc/nuke.php?__lib=user_option&__act=set&raw=3&type=%s&__output=8&fid=%s&%s=%s", type, parentFid, action, fid);
        }
        return url;
    }

    //NGA 后台好变态啊，某个板块的操作居然是反的 比如 “优惠信息 购物指南”
    private String getAction(int typ, boolean isSubscribe) {
        if (typ == 1) {
            return isSubscribe ? "del" : "add";
        } else {
            return isSubscribe ? "add" : "del";
        }
    }
}
