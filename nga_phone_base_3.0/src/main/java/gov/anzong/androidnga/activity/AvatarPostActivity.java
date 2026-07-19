package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.ToastUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.param.AvatarPostAction;
import sp.phone.param.HttpPostClient;
import sp.phone.task.AvatarFileUploadTask;
import sp.phone.task.ChangeAvatarLoadTask;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.ImageUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

public class AvatarPostActivity extends BaseActivity implements
        AvatarFileUploadTask.onFileUploaded, ChangeAvatarLoadTask.ChangeAvatarLoadCompleteCallBack {

    final int REQUEST_CODE_SELECT_PIC = 1;
    private final String LOG_TAG = Activity.class.getSimpleName();
    private final Object lock = new Object();
    private final HashSet<String> urlSet = new HashSet<String>();
    // private Button button_commit;
    // private Button button_cancel;
    // private ImageButton button_upload;
    // private ImageButton button_emotion;
    Object commit_lock = new Object();
    private EditText titleText;
    private AvatarPostAction act;
    private TextView add_title, avatarpreview;
    private ImageView avatarImage;
    private Button selectpic_button, submit_button;
    private Bitmap resultbitmap;
    private String REPLY_URL = "http://nga.178.com/nuke.php?";
    private View v;
    private boolean loading;
    private AvatarFileUploadTask uploadTask = null;
    private ButtonCommitListener commitListener = null;

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        v = this.getLayoutInflater().inflate(R.layout.activity_change_avatar, null);
        v.setBackgroundColor(getResources().getColor(
                ThemeManager.getInstance().getBackgroundColor()));
        this.setContentView(v);

        act = new AvatarPostAction();
        loading = false;

        titleText = (EditText) findViewById(R.id.urladd);
        add_title = (TextView) findViewById(R.id.add_title);
        avatarpreview = (TextView) findViewById(R.id.avatarpreview);
        avatarImage = (ImageView) findViewById(R.id.avatarImage);
        selectpic_button = (Button) findViewById(R.id.selectpic_button);
        submit_button = (Button) findViewById(R.id.submit_button);
        titleText.setSelected(true);
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.isNightMode()) {
            titleText.setBackgroundResource(tm.getBackgroundColor());
            int textColor = this.getResources().getColor(
                    tm.getForegroundColor());
            add_title.setTextColor(textColor);
            avatarpreview.setTextColor(textColor);
            titleText.setTextColor(textColor);
        }
        avatarImage.setVisibility(View.GONE);
        avatarpreview.setVisibility(View.GONE);
        selectpic_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
            }

        });
        submit_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (commitListener == null) {
                    commitListener = new ButtonCommitListener(REPLY_URL);
                }
                commitListener.onClick(null);
            }

        });

        getSupportActionBar().setTitle("更改头像");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_PIC:
                NLog.i(LOG_TAG, " select file :" + data.getDataString());
                uploadTask = new AvatarFileUploadTask(this, this, data.getData());
                break;
            default:
                ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        titleText.requestFocus();
        if (uploadTask != null) {
            AvatarFileUploadTask temp = uploadTask;
            uploadTask = null;
            RunParallel(temp);
        }
        super.onResume();
    }

    @TargetApi(11)
    private void RunParallel(AvatarFileUploadTask task) {
        task.executeOnExecutor(AvatarFileUploadTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int finishUpload(String picUrl, Uri uri) {
        titleText.setText(picUrl);
        avatarImage.setVisibility(View.VISIBLE);
        avatarpreview.setVisibility(View.VISIBLE);
        handleAvatar(avatarImage, picUrl);
        return 1;
    }

    private void handleAvatar(ImageView avatarIV, String avatarUrl) {
        final String userId = UserManagerImpl.getInstance().getUserId();
        if (!StringUtils.isEmpty(avatarUrl)) {
            final String avatarPath = ImageUtils.newImage(avatarUrl, userId);
            new ChangeAvatarLoadTask(avatarIV, 0, this)
                    .execute(avatarUrl, avatarPath, userId);
        } else {
            avatarImage.setVisibility(View.GONE);
            avatarpreview.setVisibility(View.GONE);
        }

    }

    @Override
    public void OnAvatarLoadStart(String url) {
        // TODO Auto-generated method stub

        synchronized (lock) {
            this.urlSet.add(url);
        }
    }

    @Override
    public void OnAvatarLoadComplete(String url, Bitmap result) {
        // TODO Auto-generated method stub
        synchronized (lock) {
            this.urlSet.remove(url);
            if (result != null) {
                resultbitmap = result;
            } else {
                avatarImage.setVisibility(View.GONE);
                avatarpreview.setVisibility(View.GONE);
            }
        }
    }

    class ButtonCommitListener implements OnClickListener {

        private final String url;

        ButtonCommitListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            if (titleText.getText().toString().length() > 0) {
                if (titleText.getText().toString().startsWith("http")) {
                    synchronized (commit_lock) {
                        if (loading == true) {
                            ToastUtils.showToast(R.string.avoidWindfury);
                            return;
                        }
                        loading = true;
                    }
                    handleReply(v);
                } else {
                    ToastUtils.showToast("请输入正确的头像URL地址");
                }
            } else {
                ToastUtils.showToast("请输入正确的头像URL地址");
            }
        }

        public void handleReply(View v1) {

            act.seticon_(titleText.getText().toString());
            new ArticlePostTask(AvatarPostActivity.this).execute(url,
                    act.toString());

        }

    }

    private class ArticlePostTask extends AsyncTask<String, Integer, String> {

        final Context c;
        private boolean keepActivity = false;

        public ArticlePostTask(Context context) {
            super();
            this.c = context;
        }

        @Override
        protected void onPreExecute() {
            ActivityUtils.getInstance().noticeSaying(c);
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            synchronized (commit_lock) {
                loading = false;
            }
            ActivityUtils.getInstance().dismiss();
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String result) {
            synchronized (commit_lock) {
                loading = false;
            }
            ActivityUtils.getInstance().dismiss();
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length < 2)
                return "parameter error";
            String ret = "网络错误";
            String url = params[0];
            String body = params[1];

            HttpPostClient c = new HttpPostClient(url);
            String cookie = PhoneConfiguration.getInstance().getCookie();
            c.setCookie(cookie);
            try {
                InputStream input = null;
                HttpURLConnection conn = c.post_body(body);
                if (conn != null) {
                    if (conn.getResponseCode() >= 500) {
                        input = null;
                        keepActivity = true;
                        ret = "二哥在用服务器下毛片";
                    } else {
                        if (conn.getResponseCode() >= 400) {
                            input = conn.getErrorStream();
                            keepActivity = true;
                        } else
                            input = conn.getInputStream();
                    }
                } else
                    keepActivity = true;

                if (input != null) {
                    String html = IOUtils.toString(input, "gbk");
                    ret = getReplyResult(html);
                } else
                    keepActivity = true;
            } catch (IOException e) {
                keepActivity = true;
                NLog.e(LOG_TAG, NLog.getStackTraceString(e));

            }
            return ret;
        }


        private String getReplyResult(String js) {
            if (null == js) {
                return "发送失败";
            }
            js = js.replaceAll("window.script_muti_get_var_store=", "");
            if (js.indexOf("/*error fill content") > 0)
                js = js.substring(0, js.indexOf("/*error fill content"));
            js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
            js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
            js = js.replaceAll("/\\*\\$js\\$\\*/", "");
            JSONObject o = null;
            try {
                o = (JSONObject) JSON.parseObject(js).get("data");
            } catch (Exception e) {
                NLog.e("TAG", "can not parse :\n" + js);
            }
            if (o == null) {
                try {
                    o = (JSONObject) JSON.parseObject(js).get("error");
                } catch (Exception e) {
                    NLog.e("TAG", "can not parse :\n" + js);
                }
                if (o == null) {
                    return "发送失败";
                }
                return o.getString("0");
            }
            return o.getString("0");
        }

        @Override
        protected void onPostExecute(String result) {
            String success_results[] = {"操作成功 你可能需要重新登录以显示新的头像"};
            if (keepActivity == false) {
                boolean success = false;
                for (int i = 0; i < success_results.length; ++i) {
                    if (result.contains(success_results[i])) {
                        success = true;
                        break;
                    }
                }
                if (!success)
                    keepActivity = true;
            }
            ToastUtils.showToast("操作成功");
            ActivityUtils.getInstance().dismiss();
            String userId = UserManagerImpl.getInstance().getUserId();
//            String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
//            HttpUtil.downImage3(resultbitmap, avatarPath);
            if (!keepActivity) {
                Intent intent = new Intent();
                AvatarPostActivity.this.setResult(123, intent);
                intent.putExtra("avatar", act.geticon_());
                AvatarPostActivity.this.finish();
            }
            synchronized (commit_lock) {
                loading = false;
            }

            super.onPostExecute(result);
        }

    }

}