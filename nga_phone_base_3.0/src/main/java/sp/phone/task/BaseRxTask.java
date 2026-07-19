package sp.phone.task;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Subscription;

import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;

public abstract class BaseRxTask {

    private RetrofitService mService;

    private Subscription mSubscription;

    private LifecycleProvider<FragmentEvent> mLifecycleProvider;

    public BaseRxTask() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    public void setLifecycleProvider(LifecycleProvider<FragmentEvent> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
    }

    public void post(String url, OnHttpCallBack<String> callBack) {
        Observable<String> observable = mService.post(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        if (mLifecycleProvider != null) {
            observable = observable.compose(mLifecycleProvider.bindUntilEvent(FragmentEvent.DETACH));
        }

        observable.subscribe(new BaseSubscriber<String>() {

            @Override
            public void onError(@NonNull Throwable throwable) {
                mSubscription = null;
                callBack.onError(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                mSubscription = null;
            }

            @Override
            public void onNext(@NonNull String s) {
                mSubscription = null;
                callBack.onSuccess(s);
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                super.onSubscribe(subscription);
                mSubscription = subscription;
            }
        });
    }


    public void get(String url, OnHttpCallBack<String> callBack) {
        Observable<String> observable = mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        if (mLifecycleProvider != null) {
            observable = observable.compose(mLifecycleProvider.bindUntilEvent(FragmentEvent.DETACH));
        }

        observable.subscribe(new BaseSubscriber<String>() {

            @Override
            public void onError(@NonNull Throwable throwable) {
                mSubscription = null;
                callBack.onError(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                mSubscription = null;
            }

            @Override
            public void onNext(@NonNull String s) {
                mSubscription = null;
                callBack.onSuccess(s);
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                super.onSubscribe(subscription);
                mSubscription = subscription;
            }
        });
    }


    public void cancel() {
        if (mSubscription != null) {
            mSubscription.cancel();
            mSubscription = null;
        }
    }
}
