package sp.phone.mvp.model;

import android.text.TextUtils;

import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.bean.ThreadData;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.model.convert.ArticleConvertFactory;
import sp.phone.mvp.model.convert.ErrorConvertFactory;
import sp.phone.param.ArticleListParam;
import sp.phone.rxjava.BaseSubscriber;
import gov.anzong.androidnga.common.util.NLog;

/**
 * 加载帖子内容
 * Created by Justwen on 2017/7/10.
 */

public class ArticleListModel extends BaseModel implements ArticleListContract.Model {

    private static final String TAG = ArticleListModel.class.getSimpleName();

    private RetrofitService mService;

    public ArticleListModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    public String getUrl(ArticleListParam param) {
        int page = param.page;
        int tid = param.tid;
        int pid = param.pid;
        int authorId = param.authorId;
        String url = getAvailableDomain() + "/read.php?" + "&page=" + page + "&__output=8&noprefix&v2";
        if (tid != 0) {
            url = url + "&tid=" + tid;
        }
        if (pid != 0) {
            url = url + "&pid=" + pid;
        }

        if (authorId != 0) {
            url = url + "&authorid=" + authorId;
        }

        return url;

    }

    @Override
    public void loadPage(ArticleListParam param, final OnHttpCallBack<ThreadData> callBack) {
        loadPage(param, null, callBack);
    }

    @Override
    public void loadPage(ArticleListParam param, Map<String, String> header, OnHttpCallBack<ThreadData> callBack) {
        String url = getUrl(param);
        mService.get(url, header)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .compose(getLifecycleProvider().<String>bindUntilEvent(FragmentEvent.DETACH))
                .map(new Function<String, ThreadData>() {
                    @Override
                    public ThreadData apply(@NonNull String s) throws Exception {
                        long time = System.currentTimeMillis();
                        ThreadData data = ArticleConvertFactory.getArticleInfo(s);
                        NLog.e(TAG, "time = " + (System.currentTimeMillis() - time));
                        if (data == null) {
                            String errorMsg = ErrorConvertFactory.getErrorMessage(s);
                            if (errorMsg != null) {
                                throw new Exception(errorMsg);
                            } else {
                                throw new ServerException("NGA后台抽风了，请尝试右上角菜单中的使用内置浏览器打开");
                            }
                        } else {
                            return data;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getLifecycleProvider().<ThreadData>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<ThreadData>() {

                    @Override
                    public void onNext(@NonNull ThreadData threadData) {
                        callBack.onSuccess(threadData);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(ErrorConvertFactory.getErrorMessage(throwable), throwable);
                    }
                });
    }

    @Override
    public void cachePage(ArticleListParam param, String rawData) {

        if (TextUtils.isEmpty(param.topicInfo)) {
            ToastUtils.error("缓存失败！");
            return;
        }
        ThreadUtils.postOnSubThread(() -> {
            try {
                String path = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/" + param.tid;
                File describeFile = new File(path, param.tid + ".json");
                FileUtils.write(describeFile, param.topicInfo);
                File rawDataFile = new File(path, param.page + ".json");
                FileUtils.write(rawDataFile, rawData);
                ToastUtils.success("缓存成功！");
            } catch (IOException e) {
                ToastUtils.error("缓存失败！");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void loadCachePage(ArticleListParam param, OnHttpCallBack<ThreadData> callBack) {
        Observable.create((ObservableOnSubscribe<ThreadData>) emitter -> {
            String cachePath = ContextUtils.getContext().getFilesDir().getAbsolutePath()
                    + "/cache/" + param.tid + "/" + param.page + ".json";
            File cacheFile = new File(cachePath);
            String rawData = FileUtils.readFileToString(cacheFile);
            ThreadData threadData = ArticleConvertFactory.getArticleInfo(rawData);
            if (threadData != null) {
                emitter.onNext(threadData);
            } else {
                emitter.onError(new Exception());
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<ThreadData>() {
                    @Override
                    public void onNext(ThreadData threadData) {
                        callBack.onSuccess(threadData);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError("读取缓存失败！");
                    }
                });
    }

    public static class ServerException extends Exception {

        public ServerException(String message) {
            super(message);
        }
    }

}
