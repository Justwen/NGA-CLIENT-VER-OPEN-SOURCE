package sp.phone.mvp.presenter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import gov.anzong.androidnga.base.util.PermissionUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.util.FileUtils;
import gov.anzong.androidnga.common.util.LogUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.mvp.model.TopicListModel;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.param.ParamKey;
import sp.phone.param.TopicListParam;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.ui.fragment.TopicCacheFragment;
import sp.phone.util.ARouterUtils;

/**
 * @author Justwen
 * @date 2017/6/3
 */

public class TopicListPresenter extends ViewModel implements LifecycleObserver {

    // Following variables are for the 24 hour hot topic feature
    // How many pages we query for twenty four hour hot topic
    protected final int twentyFourPageCount = 5;
    // How many total topics we want to show
    protected final int twentyFourTopicCount = 50;
    protected int pageQueriedCounter = 0;
    protected int twentyFourCurPos = 0;
    protected TopicListInfo twentyFourList = new TopicListInfo();
    protected TopicListInfo twentyFourCurList = new TopicListInfo();

    private TopicListParam mRequestParam;

    private MutableLiveData<TopicListInfo> mFirstTopicList = new MutableLiveData<>();

    private MutableLiveData<TopicListInfo> mNextTopicList = new MutableLiveData<>();

    private MutableLiveData<String> mErrorMsg = new MutableLiveData<>();

    private MutableLiveData<Boolean> mRefreshingState = new MutableLiveData<>();

    private MutableLiveData<ThreadPageInfo> mRemovedTopic = new MutableLiveData<>();

    private TopicListModel mBaseModel;

    private OnHttpCallBack<TopicListInfo> mCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            mErrorMsg.setValue(text);
            mRefreshingState.setValue(false);
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            mRefreshingState.setValue(false);
            mFirstTopicList.setValue(data);
        }
    };

    private OnHttpCallBack<TopicListInfo> mNextPageCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            mErrorMsg.setValue(text);
            mRefreshingState.setValue(false);
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            mRefreshingState.setValue(false);
            mNextTopicList.setValue(data);
        }
    };

    /* callback for the twenty four hour hot topic list */
    private OnHttpCallBack<TopicListInfo> mTwentyFourCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            mErrorMsg.setValue(text);
            mRefreshingState.setValue(false);
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            /* Concatenate the pages */
            twentyFourList.getThreadPageList().addAll(data.getThreadPageList());
            pageQueriedCounter++;

            if (pageQueriedCounter == twentyFourPageCount) {
                twentyFourCurPos = 0;
                List<ThreadPageInfo> threadPageList = twentyFourList.getThreadPageList();
                threadPageList.removeIf(item -> (data.curTime - item.getPostDate() > 24 * 60 * 60));
                if (threadPageList.size() > twentyFourTopicCount) {
                    threadPageList.subList(twentyFourTopicCount, threadPageList.size());
                }
                Collections.sort(twentyFourList.getThreadPageList(), (o1, o2) -> Integer.compare(o2.getReplies(), o1.getReplies()));
                // We list 20 topics each time
                int endPos = Math.min(twentyFourCurPos + 20, twentyFourList.getThreadPageList().size());
                twentyFourCurList.setThreadPageList(twentyFourList.getThreadPageList().subList(0, endPos));
                twentyFourCurPos = endPos;

                mRefreshingState.setValue(false);
                mNextTopicList.setValue(twentyFourCurList);
            }
        }
    };

    public TopicListPresenter() {
        mBaseModel = new TopicListModel();
        mBaseModel = onCreateModel();
    }

    public void setRequestParam(TopicListParam requestParam) {
        mRequestParam = requestParam;
    }

    public MutableLiveData<TopicListInfo> getFirstTopicList() {
        return mFirstTopicList;
    }

    public MutableLiveData<TopicListInfo> getNextTopicList() {
        return mNextTopicList;
    }

    public MutableLiveData<Boolean> isRefreshing() {
        return mRefreshingState;
    }

    public MutableLiveData<String> getErrorMsg() {
        return mErrorMsg;
    }

    public MutableLiveData<ThreadPageInfo> getRemovedTopic() {
        return mRemovedTopic;
    }

    protected TopicListModel onCreateModel() {
        return new TopicListModel();
    }

    public void removeTopic(ThreadPageInfo info, final int position) {
        mBaseModel.removeTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                mErrorMsg.setValue(text);
            }

            @Override
            public void onSuccess(String data) {
                ToastUtils.show(data);
                mRemovedTopic.setValue(info);
            }
        });
    }

    public void removeCacheTopic(ThreadPageInfo info) {
        mBaseModel.removeCacheTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                mErrorMsg.postValue("删除失败！");
            }

            @Override
            public void onSuccess(String data) {
                ToastUtils.showToast("删除成功！");
                mRemovedTopic.postValue(info);
            }
        });

    }

    public void loadPage(int page, TopicListParam requestInfo) {
        mRefreshingState.setValue(true);
        if (requestInfo.twentyfour == 1) {
            // preload pages
            twentyFourList.getThreadPageList().clear();
            pageQueriedCounter = 0;
            mBaseModel.loadTwentyFourList(requestInfo, mTwentyFourCallBack, twentyFourPageCount);
        } else {
            mBaseModel.loadTopicList(page, requestInfo, mCallBack);
        }
    }

    public void loadCachePage() {
        mBaseModel.loadCache(mCallBack);
    }

    public void loadNextPage(int page, TopicListParam requestInfo) {
        mRefreshingState.setValue(true);
        if (requestInfo.twentyfour == 1) {
            int endPos = Math.min(twentyFourCurPos + 20, twentyFourList.getThreadPageList().size());
            twentyFourCurList.setThreadPageList(twentyFourList.getThreadPageList().subList(0, endPos));
            twentyFourCurPos = endPos;
            mRefreshingState.setValue(false);
            mNextTopicList.setValue(twentyFourCurList);
        } else {
            mBaseModel.loadTopicList(page, requestInfo, mNextPageCallBack);
        }
    }

    public boolean isBookmarkBoard(int fid, int stid) {
        return ForumBoardViewModel.INSTANCE.isBookmarkBoard(fid, stid);
    }

    public void addBookmarkBoard() {
        ForumBoardViewModel.INSTANCE.addBookmarkBoard(mRequestParam.title, mRequestParam.fid, mRequestParam.stid, mRequestParam.boardHead);
    }

    public void removeBookmarkBoard(int fid, int stid) {
        ForumBoardViewModel.INSTANCE.removeBookmarkBoard(mRequestParam.fid, mRequestParam.stid);
    }

    public void startArticleActivity(String tid, String title) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_CONTENT)
                .withInt(ParamKey.KEY_TID, Integer.parseInt(tid))
                .withString(ParamKey.KEY_TITLE, title)
                .navigation(ContextUtils.getContext());
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    public void onViewCreated() {
        if (mRequestParam != null && mRequestParam.loadCache) {
            loadCachePage();
        } else {
            loadPage(1, mRequestParam);
        }
    }

    public void exportCacheTopic(Fragment fragment) {
        PermissionUtils.requestAsync(fragment, new BaseSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    String srcDir = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/";

                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String dateStr = dateFormat.format(new Date(System.currentTimeMillis()));
                    String destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator
                            + BuildConfig.APPLICATION_ID + File.separator + "cache/cache_" + dateStr + ".zip";

                    if (FileUtils.zipFiles(srcDir, destDir)) {
                        ToastUtils.success("导出成功至" + destDir);
                    } else {
                        ToastUtils.error("导出失败");
                    }
                } else {
                    ToastUtils.warn("无存储权限，无法导出！");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void showFileChooser(Fragment fragment) {
        PermissionUtils.request(fragment, new BaseSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        fragment.startActivityForResult(intent, TopicCacheFragment.REQUEST_IMPORT_CACHE);
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.warn("系统不支持导入");
                    }
                } else {
                    ToastUtils.warn("无存储权限，无法导入！");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    public void importCacheTopic(Uri uri) {
        Context context = ContextUtils.getContext();
        if (!checkCacheZipFile(context, uri)) {
            ToastUtils.error("选择非法文件");
            return;
        }
        ContentResolver cr = context.getContentResolver();
        String destDir = context.getFilesDir().getAbsolutePath();
        File tempZipFile = new File(destDir, "temp.zip");
        try (InputStream is = cr.openInputStream(uri)) {
            if (is == null) {
                return;
            }
            org.apache.commons.io.FileUtils.copyInputStreamToFile(is, tempZipFile);
            FileUtils.unzip(tempZipFile.getAbsolutePath(), destDir);
            loadCachePage();
            ToastUtils.success("导入成功！！");
        } catch (Exception e) {
            LogUtils.print(e);
        }
        tempZipFile.delete();
    }

    private boolean checkCacheZipFile(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        String contentType = cr.getType(uri);
        return contentType != null && contentType.contains("zip");
    }

    public String getBoardName(int fid, int stid) {
        return ForumBoardViewModel.INSTANCE.getBoardName(fid, stid);
    }
}
