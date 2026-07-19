package sp.phone.mvp.model;

import com.alibaba.fastjson.JSON;
import com.justwen.androidnga.cloud.CloudServerManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.mvp.model.convert.ErrorConvertFactory;
import sp.phone.mvp.model.convert.TopicConvertFactory;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.param.TopicListParam;
import sp.phone.rxjava.BaseSubscriber;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/11/21.
 */

public class TopicListModel extends BaseModel implements TopicListContract.Model {

    private RetrofitService mService;

    private Map<String, String> mFieldMap;

    private TopicConvertFactory mConvertFactory;

    public TopicListModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
        mConvertFactory = new TopicConvertFactory();
    }

    private void initFieldMap() {
        if (mFieldMap == null) {
            mFieldMap = new HashMap<>();
            mFieldMap.put("__lib", "topic_favor");
            mFieldMap.put("__act", "topic_favor");
            mFieldMap.put("__output", "8");
            mFieldMap.put("action", "del");
        }
    }

    @Override
    public void loadCache(OnHttpCallBack<TopicListInfo> callBack) {
        Observable.create((ObservableOnSubscribe<TopicListInfo>) emitter -> {
            String path = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/";
            File[] cacheDirs = new File(path).listFiles();

            if (cacheDirs == null) {
                emitter.onError(new Exception());
            } else {
                TopicListInfo listInfo = new TopicListInfo();
                for (File dir : cacheDirs) {
                    File infoFile = new File(dir, dir.getName() + ".json");
                    if (!infoFile.exists()) {
                        continue;
                    }
                    String rawData = FileUtils.readFileToString(infoFile);
                    ThreadPageInfo pageInfo = JSON.parseObject(rawData, ThreadPageInfo.class);
                    if (pageInfo == null) {
                        CloudServerManager.putCrashData(ContextUtils.getContext(),"rawData", rawData);
                    } else {
                        listInfo.addThreadPage(JSON.parseObject(rawData, ThreadPageInfo.class));
                    }
                }
                emitter.onNext(listInfo);
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TopicListInfo>() {
                    @Override
                    public void onNext(TopicListInfo topicListInfo) {
                        callBack.onSuccess(topicListInfo);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError("读取缓存失败！");
                    }
                });
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final OnHttpCallBack<String> callBack) {
        initFieldMap();
        mFieldMap.put("page", String.valueOf(info.getPage()));
        String tidArray = String.valueOf(info.getTid());
        if (info.getPid() != 0) {
            tidArray = tidArray +  "_" + info.getPid();
        }
        mFieldMap.put("tidarray", tidArray);
        mService.post(mFieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("操作成功")) {
                            callBack.onSuccess("操作成功！");
                        } else {
                            callBack.onError("操作失败!");
                        }
                    }
                });
    }

    @Override
    public void loadTopicList(final int page, TopicListParam param, final OnHttpCallBack<TopicListInfo> callBack) {
        String url = getUrl(page, param);
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, TopicListInfo>() {
                    @Override
                    public TopicListInfo apply(@NonNull String js) throws Exception {
                        //NLog.d(js);
                        TopicListInfo result = mConvertFactory.getTopicListInfo(js, page);
                        if (result != null) {
                            return result;
                        } else {
                            throw new Exception(ErrorConvertFactory.getErrorMessage(js));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TopicListInfo>() {
                    @Override
                    public void onNext(@NonNull TopicListInfo topicListInfo) {
                        callBack.onSuccess(topicListInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(ErrorConvertFactory.getErrorMessage(throwable));
                    }
                });
    }

    @Override
    public void loadTwentyFourList(TopicListParam param, final OnHttpCallBack<TopicListInfo> callBack, int totalPage) {
        List<Observable<String>> obsList = new ArrayList<Observable<String>>();
        for (int i = 1; i <= totalPage; i++) {
            obsList.add(mService.get(getUrl(i, param)));
        }
        Observable.concat(obsList).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, TopicListInfo>() {
                    @Override
                    public TopicListInfo apply(@NonNull String js) throws Exception {
                        NLog.d(js);
                        TopicListInfo result = mConvertFactory.getTopicListInfo(js, 0);
                        if (result != null) {
                            return result;
                        } else {
                            throw new Exception(ErrorConvertFactory.getErrorMessage(js));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TopicListInfo>() {
                    @Override
                    public void onNext(@NonNull TopicListInfo topicListInfo) {
                        callBack.onSuccess(topicListInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(ErrorConvertFactory.getErrorMessage(throwable));
                    }
                });
    }

    @Override
    public void removeCacheTopic(ThreadPageInfo info, OnHttpCallBack<String> callBack) {
        ThreadUtils.postOnSubThread(() -> {
            String path = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/";
            File[] cacheDirs = new File(path).listFiles();
            if (cacheDirs == null) {
                callBack.onError(null);
                return;
            }
            try {
                for (File dir : cacheDirs) {
                    if (dir.getName().equals(String.valueOf(info.getTid()))) {
                        FileUtils.deleteDirectory(dir);
                        callBack.onSuccess(null);
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.onError(null);

        });
    }

    private String getUrl(int page, TopicListParam requestInfo) {
        StringBuilder jsonUri = new StringBuilder(getAvailableDomain() + "/thread.php?");
        if (0 != requestInfo.authorId) {
            jsonUri.append("authorid=").append(requestInfo.authorId).append("&");
        }
        if (requestInfo.searchPost != 0) {
            jsonUri.append("searchpost=").append(requestInfo.searchPost).append("&");
        }
        if (requestInfo.favor != 0) {
            jsonUri.append("favor=").append(requestInfo.favor).append("&");
        }
        if (requestInfo.content != 0) {
            jsonUri.append("content=").append(requestInfo.content).append("&");
        }

        if (!StringUtils.isEmpty(requestInfo.author)) {
            try {
                if (requestInfo.author.endsWith("&searchpost=1")) {
                    jsonUri.append("author=").append(URLEncoder.encode(
                            requestInfo.author.substring(0, requestInfo.author.length() - 13),
                            "GBK")).append("&searchpost=1&");
                } else {
                    jsonUri.append("author=").append(URLEncoder.encode(requestInfo.author, "GBK")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (requestInfo.stid != 0) {
                jsonUri.append("stid=").append(requestInfo.stid).append("&");
            } else if (0 != requestInfo.fid) {
                jsonUri.append("fid=").append(requestInfo.fid).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.key)) {
                jsonUri.append("key=").append(StringUtils.encodeUrl(requestInfo.key, "UTF-8")).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.fidGroup)) {
                jsonUri.append("fidgroup=").append(requestInfo.fidGroup).append("&");
            }

        }
        jsonUri.append("page=").append(page).append("&lite=js&noprefix");
        if (requestInfo.recommend == 1) {
            jsonUri.append("&recommend=1&order_by=postdatedesc&user=1");
        }
        return jsonUri.toString();
    }
}
