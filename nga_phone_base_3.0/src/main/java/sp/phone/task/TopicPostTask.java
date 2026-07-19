package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.param.HttpPostClient;
import gov.anzong.androidnga.common.util.NLog;

/**
 * Created by Justwen on 2017/6/6.
 */

public class TopicPostTask extends AsyncTask<String, Integer, String> {

    private final Context mContext;

    private static final String LOG_TAG = TopicPostTask.class.getSimpleName();

    private final String result_start_tag = "<title>";

    private final String result_end_tag = "</title>";

    private String mReplyUrl = Utils.getNGAHost() + "post.php?";

    private boolean mHasError = false;

    private CallBack mCallBack;


    public interface CallBack {

        void onArticlePostFinished(boolean isSuccess, String result);
    }

    public TopicPostTask(Context context, CallBack callBack) {
        super();
        mContext = context;
        mCallBack = callBack;
    }

    @Override
    protected void onCancelled() {
        mCallBack.onArticlePostFinished(false, null);
        super.onCancelled();
    }

    @Override
    protected void onCancelled(String result) {
        mCallBack.onArticlePostFinished(false, null);
        super.onCancelled();
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length < 1)
            return "parameter error";
        String ret = "网络错误";
        String url = mReplyUrl;
        String body = params[0];

        HttpPostClient c = new HttpPostClient(url);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn != null) {
                if (conn.getResponseCode() >= 500) {
                    input = null;
                    mHasError = true;
                    ret = "二哥在用服务器下毛片";
                } else {
                    if (conn.getResponseCode() >= 400) {
                        input = conn.getErrorStream();
                        mHasError = true;
                    } else
                        input = conn.getInputStream();
                }
            } else
                mHasError = true;

            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                ret = getReplyResult(html);
            } else
                mHasError = true;
        } catch (IOException e) {
            mHasError = true;
            NLog.e(LOG_TAG, NLog.getStackTraceString(e));
        }
        return ret;
    }

    private String getReplyResult(String html) {
        int start = html.indexOf(result_start_tag);
        if (start == -1)
            return "发帖失败";
        start += result_start_tag.length();
        int end = html.indexOf(result_end_tag, start);
        if (end < 0)
            return "发帖失败";
        return html.substring(start, end);
    }

    @Override
    protected void onPostExecute(String result) {
        String success_results[] = {"发贴完毕", "@提醒每24小时不能超过50个"};
        if (!mHasError) {
            boolean success = false;
            for (String success_result : success_results) {
                if (result.contains(success_result)) {
                    success = true;
                    result = success_result;
                    break;
                }
            }
            if (!success)
                mHasError = true;
        }
        mCallBack.onArticlePostFinished(!mHasError, result);
        super.onPostExecute(result);
    }
}
