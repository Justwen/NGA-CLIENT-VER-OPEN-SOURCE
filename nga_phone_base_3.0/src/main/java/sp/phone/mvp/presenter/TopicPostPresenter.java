package sp.phone.mvp.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.model.TopicPostModel;
import sp.phone.param.PostParam;
import sp.phone.task.TopicPostTask;
import sp.phone.ui.fragment.TopicPostFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

public class TopicPostPresenter extends BasePresenter<TopicPostFragment, TopicPostModel>
        implements TopicPostContract.Presenter, TopicPostTask.CallBack {

    private boolean mLoading;

    private PostParam mPostParam;

    @Override
    public void setEmoticon(String emotion) {
        String[] emotions = emotion.split("-");
        try (InputStream is = mBaseView.getContext().getResources().getAssets().open(emotions[1])) {
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Drawable drawable = new BitmapDrawable(mBaseView.getContext().getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                SpannableString spanString = new SpannableString(emotions[0]);
                ImageSpan span = new ImageSpan(drawable,
                        ImageSpan.ALIGN_BASELINE);
                spanString.setSpan(span, 0, emotions[0].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mBaseView.insertBodyText(spanString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }//

    @Override
    public void setPostParam(PostParam postParam) {
        mPostParam = postParam;
        mBaseModel.getPostInfo(mPostParam, new OnHttpCallBack<PostParam>() {
            @Override
            public void onError(String text) {
                if (mBaseView != null) {
                    ActivityUtils.showToast(text);
                }
            }

            @Override
            public void onSuccess(PostParam data) {
                mPostParam = data;
            }
        });
    }

    @Override
    public void onViewCreated() {
        if (!TextUtils.isEmpty(mPostParam.getPostSubject())) {
            mBaseView.insertTitleText(mPostParam.getPostSubject());
        }
        if (!TextUtils.isEmpty(mPostParam.getPostContent())) {
            mBaseView.insertBodyText(mPostParam.getPostContent());
        }
        super.onViewCreated();
    }

    @Override
    public void post(String title, String body, boolean isAnony) {
        if (mLoading) {
            mBaseView.showToast(R.string.avoidWindfury);
            return;
        }
        mLoading = true;
        mPostParam.setAnonymous(isAnony);
        mPostParam.setPostSubject(title);
        if (!body.isEmpty()) {
            mPostParam.setPostContent(FunctionUtils.ColorTxtCheck(body));
            mBaseModel.post(mPostParam, this);
        }
    }

    @Override
    public void showFilePicker() {
        mBaseView.showFilePicker();
    }

    @Override
    public void startUploadTask(final Uri uri) {
        mBaseView.showUploadFileProgressBar();
        mBaseModel.uploadFile(uri, mPostParam, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (mBaseView != null) {
                    mBaseView.hideUploadFileProgressBar();
                    ToastUtils.error(text);
                }
            }

            @Override
            public void onSuccess(String data) {
                if (mBaseView != null) {
                    mBaseView.hideUploadFileProgressBar();
                    ToastUtils.success("上传成功");
                    finishUpload(data, uri);
                }
            }
        });
    }

    @Override
    public void insertAtFormat() {
        mBaseView.insertBodyText("[@]", 2);
    }

    @Override
    public void insertQuoteFormat() {
        mBaseView.insertBodyText("[quote][/quote]", "[quote]".length());
    }

    @Override
    public void insertUrlFormat() {
        mBaseView.insertBodyText("[url][/url]", "[url]".length());
    }

    @Override
    public void insertBoldFormat() {
        mBaseView.insertBodyText("[b][/b]", "[b]".length());
    }

    @Override
    public void insertItalicFormat() {
        mBaseView.insertBodyText("[i][/i]", "[i]".length());
    }

    @Override
    public void insertUnderLineFormat() {
        mBaseView.insertBodyText("[u][/u]", "[u]".length());
    }

    @Override
    public void insertDeleteLineFormat() {
        mBaseView.insertBodyText("[del][/del]", "[del]".length());
    }

    @Override
    public void insertCollapseFormat() {
        mBaseView.insertBodyText("[collapse][/collapse]", "[collapse]".length());
    }

    @Override
    public void insertFontColorFormat(String fontColor) {
        mBaseView.insertBodyText(fontColor, fontColor.length() - "[/color]".length());
    }

    @Override
    public void insertFontSizeFormat(String fontSize) {
        mBaseView.insertBodyText(fontSize, "[size=100%]".length());
    }

    @Override
    public void insertTopicCategory(String category) {
        mBaseView.insertTitleText(category);
    }

    @Override
    public void loadTopicCategory(OnHttpCallBack<List<String>> callBack) {
        mBaseModel.loadTopicCategory(mPostParam, callBack);
    }

    @Override
    public void onArticlePostFinished(boolean isSuccess, String result) {
        ActivityUtils.getInstance().dismiss();
        if (mBaseView != null) {
            if (!StringUtils.isEmpty(result)) {
                mBaseView.showToast(result);
            }
            if (isSuccess) {
                mBaseView.setResult(Activity.RESULT_OK);
                mBaseView.finish();
            }
        }
        mLoading = false;
    }

    private void finishUpload(String picUrl, Uri uri) {
        String selectedImagePath2 = FunctionUtils.getPath(mBaseView.getContext(), uri);
        String spanStr = "[img]./" + picUrl + "[/img]";
        if (!StringUtils.isEmpty(selectedImagePath2)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath2, options);
            DisplayMetrics dm = ContextUtils.getResources().getDisplayMetrics();

            int screenWidth = (int) (dm.widthPixels * 0.75);
            int screenHeight = (int) (dm.heightPixels * 0.75);
            int width = options.outWidth;
            int height = options.outHeight;
            float scaleWidth = ((float) screenWidth) / width;
            float scaleHeight = ((float) screenHeight) / height;
            if (scaleWidth < scaleHeight && scaleWidth < 1f) {// 不能放大啊,然后主要是哪个小缩放到哪个就行了
                options.inSampleSize = (int) (1 / scaleWidth);
            } else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
                options.inSampleSize = (int) (1 / scaleHeight);
            } else {
                options.inSampleSize = 1;
            }
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
            SpannableString spanStringS = new SpannableString(spanStr);
            ImageSpan span = new ImageSpan(bd, ImageSpan.ALIGN_BASELINE);
            spanStringS.setSpan(span, 0, spanStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBaseView.insertFile(selectedImagePath2, spanStringS);
        } else {
            mBaseView.insertFile(selectedImagePath2, picUrl);
        }
    }

    @Override
    protected TopicPostModel onCreateModel() {
        return new TopicPostModel();
    }
}