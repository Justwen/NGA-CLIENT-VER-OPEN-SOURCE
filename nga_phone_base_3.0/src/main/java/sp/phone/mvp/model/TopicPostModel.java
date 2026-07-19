package sp.phone.mvp.model;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.commons.io.IOUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import gov.anzong.androidnga.util.ToastUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import sp.phone.http.bean.TopicPostBean;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.param.ParamKey;
import sp.phone.param.PostParam;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.task.TopicPostTask;
import sp.phone.util.ImageUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

;

/**
 * Created by Justwen on 2017/6/10.
 */

public class TopicPostModel extends BaseModel implements TopicPostContract.Model {

    private String mHostUrl = Utils.getNGAHost() + "post.php?";

    private static final String BASE_URL_ATTACHMENT_SERVER = "https://img8.nga.cn/attach.php?";

    private RetrofitService mRetrofitService;

    public TopicPostModel() {
        OkHttpClient.Builder builder = RetrofitHelper.getInstance().createOkHttpClientBuilder();
        builder.connectTimeout(5, TimeUnit.MINUTES);
        mRetrofitService = RetrofitHelper.getInstance().createRetrofit(builder).create(RetrofitService.class);
    }

    @Override
    public void getPostInfo(PostParam postParam, OnHttpCallBack<PostParam> callBack) {
        StringBuilder builder = new StringBuilder(mHostUrl);
        builder.append("fid=")
                .append(postParam.getPostFid())
                .append("&lite=js");
        if (postParam.getPostAction() != null) {
            builder.append("&action=").append(postParam.getPostAction());
        }

        if (postParam.getPostPid() != null) {
            builder.append("&pid=").append(postParam.getPostPid());
        }

        if (postParam.getPostTid() != null) {
            builder.append("&tid=").append(postParam.getPostTid());
        }

        if (postParam.getStid() != null) {
            builder.append("&stid=").append(postParam.getStid());
        }

        mRetrofitService.post(builder.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map((String s) -> {
                    NLog.d(s);
                    s = s.replace("window.script_muti_get_var_store=", "");
                    TopicPostBean bean = JSON.parseObject(s, TopicPostBean.class);
                    postParam.setAuthCode(bean.getData().getAuth());
                    return postParam;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<PostParam>() {

                    @Override
                    public void onNext(PostParam postParam) {
                        callBack.onSuccess(postParam);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError("获取附件验证码失败，将无法上传附件！！");
                    }
                });
    }

    @Override
    public void loadTopicCategory(PostParam postParam, final OnHttpCallBack<List<String>> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("__lib", "topic_key");
        map.put("__act", "get");
        map.put(ParamKey.KEY_FID, String.valueOf(postParam.getPostFid()));
        map.put("__output", "8");
        mRetrofitService.get(map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    JSONObject obj = JSON.parseObject(s).getJSONObject("data").getJSONObject("0");
                    List<String> ret = new ArrayList<>();
                    for (int index = 0; obj.containsKey(String.valueOf(index)); index++) {
                        ret.add(obj.getJSONObject(String.valueOf(index)).getString("0"));
                    }
                    return ret;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<String>>() {

                    @Override
                    public void onNext(@NonNull List<String> list) {
                        callBack.onSuccess(list);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void post(PostParam postParam, TopicPostTask.CallBack callBack) {
        new TopicPostTask(ContextUtils.getContext(), callBack).execute(postParam.toString());
    }

    @Override
    public void uploadFile(Uri uri, PostParam postParam, OnHttpCallBack<String> callBack) {
        uploadFile(uri, postParam, callBack, false);

    }

    private void uploadFile(Uri uri, PostParam postParam, OnHttpCallBack<String> callBack, boolean compress) {
        Observable.just(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Uri, MultipartBody>() {
                    @Override
                    public MultipartBody apply(Uri uri) throws Exception {
                        Context context = ContextUtils.getContext();
                        ContentResolver cr = context.getContentResolver();

                        ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
                        String contentType = cr.getType(uri);
                        if (StringUtils.isEmpty(contentType)) {
                            throw new IllegalArgumentException(context.getString(R.string.invalid_img_selected));
                        }
                        String fileName = contentType.replace('/', '.');
                        long fileSize = pfd.getStatSize();
                        byte[] img;// = IOUtils.toByteArray(cr.openInputStream(uri));
                        if (compress && fileSize >= 1024 * 1024) {
                            img = ImageUtils.fitImageToUpload(cr.openInputStream(uri), cr.openInputStream(uri));
                        } else {
                            img = IOUtils.toByteArray(cr.openInputStream(uri));
                        }

                        return buildMultipartBody(fileName, img, postParam);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<MultipartBody>() {
                    @Override
                    public void onNext(MultipartBody body) {
                        uploadFileInner(body, uri, postParam, callBack, compress);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

    private void uploadFileInner(MultipartBody multipartBody, Uri uri, PostParam postParam, OnHttpCallBack<String> callBack, boolean compress) {
        if (getLifecycleProvider() == null) {
            return;
        }
        mRetrofitService.uploadFile(BASE_URL_ATTACHMENT_SERVER, multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(getLifecycleProvider().<ResponseBody>bindUntilEvent(FragmentEvent.DETACH))
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        return responseBody.string();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            s = s.replace("window.script_muti_get_var_store=", "");
                            JSONObject object = JSON.parseObject(s);
                            if (object.containsKey("error_code")) {
                                int errorCode = object.getInteger("error_code");
                                if (errorCode == 9 && !compress) {
                                    ToastUtils.showShortToast("附件过大，无法上传，重新进行压缩并上传");
                                    uploadFile(uri, postParam, callBack, true);
                                    return;
                                }
                            }
                            object = object.getJSONObject("data");
                            postParam.appendAttachment(object.getString("attachments"), object.getString("attachments_check"));
                            callBack.onSuccess(object.getString("url"));
                        } catch (Exception e) {
                            Logger.d("exception occur while uploading file " + s);
                            callBack.onError("上传图片失败，请尝试更换域名后重试");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });
    }

    private MultipartBody buildMultipartBody(String fileName, byte[] bytes, PostParam postParam) throws UnsupportedEncodingException {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")))
                .addPart(MultipartBody.Part.createFormData("attachment_file1", fileName, RequestBody.create(MediaType.parse("image/jpeg"), bytes)))
                .addFormDataPart("attachment_file1_url_utf8_name", new String(fileName.getBytes(), "UTF-8"))
                .addFormDataPart("fid", String.valueOf(postParam.getPostFid()))
                .addFormDataPart("auth", postParam.getAuthCode())
                .addFormDataPart("func", "upload")
                .addFormDataPart("v2", "1")
                .addFormDataPart("lite", "js")
                // 1 为自动缩图
                .addFormDataPart("attachment_file1_auto_size", "")
                //水印位置tl/tr/bl/br 左上右上左下右下 不设为无水印
                .addFormDataPart("attachment_file1_watermark", "")
                .addFormDataPart("attachment_file1_dscp", "")
                .addFormDataPart("attachment_file1_img", "1")
                .addFormDataPart("origin_domain", "bbs.ngacn.cc");
        return builder.build();
    }


}
