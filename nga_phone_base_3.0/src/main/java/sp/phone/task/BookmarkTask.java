package sp.phone.task;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.util.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.StringUtils;

public class BookmarkTask {

    private static final String url = Utils.getNGAHost() + "nuke.php?__lib=topic_favor&lite=js&noprefix&__act=topic_favor&action=add&tid=";

    public static void execute(int tid) {
        RetrofitService service = RetrofitHelper.getInstance().getService();
        service.post(url + tid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String result) {
                        String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
                        if (!StringUtils.isEmpty(msg)) {
                            ToastUtils.showShortToast(msg.trim());
                        }
                    }
                });
    }

    public static void execute(String tid, String pid) {
        RetrofitService service = RetrofitHelper.getInstance().getService();
        String postUrl = url + tid + "&pid=" + pid;
        service.post(postUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String result) {
                        String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
                        if (!StringUtils.isEmpty(msg)) {
                            ToastUtils.showShortToast(msg.trim());
                        }
                    }
                });
    }

}
