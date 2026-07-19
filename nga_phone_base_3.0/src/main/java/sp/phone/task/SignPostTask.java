package sp.phone.task;

import org.reactivestreams.Subscription;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.param.SignPostParam;
import sp.phone.rxjava.BaseSubscriber;

/**
 * Created by Justwen on 2018/7/28.
 */
public class SignPostTask {

    private final RetrofitService mService;

    private final Map<String, String> mParamMap = new HashMap<>();

    private Subscription mSubscription;

    public SignPostTask() {
        mService = RetrofitHelper.getInstance().getService();
        mParamMap.put("__lib", "set_sign");
        mParamMap.put("__act", "set");
        mParamMap.put("raw", "3");
        mParamMap.put("lite", "js");
        mParamMap.put("charset", "gbk");
    }

    public void execute(SignPostParam postParam, OnHttpCallBack<String> callBack) {
        if (isRunning()) {
            return;
        }
        String sign = postParam.getSign();
        try {
            sign = URLEncoder.encode(sign, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mParamMap.put("uid", postParam.getUid());
        mParamMap.put("sign", sign);
        mService.post(mParamMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {

                    @Override
                    public void onNext(String s) {
                        if (s.contains("操作成功")) {
                            callBack.onSuccess("修改成功！");

                        } else {
                            callBack.onError(s);
                        }
                    }

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        super.onSubscribe(subscription);
                        mSubscription = subscription;
                    }

                    @Override
                    public void onComplete() {
                        mSubscription = null;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mSubscription = null;
                        callBack.onError(throwable.getMessage());
                    }
                });
    }

    public void cancel() {
        if (mSubscription != null) {
            mSubscription.cancel();
            mSubscription = null;
        }
    }

    public boolean isRunning() {
        return mSubscription != null;
    }
}
