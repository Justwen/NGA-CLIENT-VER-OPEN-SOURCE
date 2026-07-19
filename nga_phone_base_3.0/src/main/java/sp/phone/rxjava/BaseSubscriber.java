package sp.phone.rxjava;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import gov.anzong.androidnga.common.util.NLog;

// 兼容 RxJava 1 和 RxJava 2
public abstract class BaseSubscriber<T> implements Observer<T>, FlowableSubscriber<T> {

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        NLog.e(throwable.getMessage());

    }

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {

    }

    @Override
    public void onSubscribe(@NonNull Subscription subscription) {
        subscription.request(Integer.MAX_VALUE);
    }
}
