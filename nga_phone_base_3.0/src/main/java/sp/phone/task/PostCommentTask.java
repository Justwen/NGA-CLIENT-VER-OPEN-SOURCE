package sp.phone.task;

import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.param.HttpPostClient;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

public class PostCommentTask extends AsyncTask<String, Integer, String> {

    public interface OnPostCommentFinishedListener {
        void OnPostCommentFinished(String result, boolean success);
    }

    private static final String postCommentUri = Utils.getNGAHost() + "post.php";
    private final int pid;
    private final int tid;
    private final int fid;
    private final String prefix;
    final private FragmentActivity fragmentActivity;
    int anonymode;
    boolean success;
    OnPostCommentFinishedListener notifier;

    public PostCommentTask(int fid, int pid, int tid, int anonymode,
                           String prefix, FragmentActivity fragmentActivity,
                           OnPostCommentFinishedListener notifier) {
        this.fid = fid;
        this.pid = pid;
        this.tid = tid;
        this.prefix = prefix;
        this.fragmentActivity = fragmentActivity;
        this.notifier = notifier;
        this.anonymode = anonymode;
    }

    @Override
    protected String doInBackground(String... params) {
        String comment = params[0];
        if (!StringUtils.isEmpty(prefix)) {
            comment = prefix + comment;
        }
        HttpPostClient c = new HttpPostClient(postCommentUri);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        final String body = this.buildBody(comment);
        String ret = null;
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn != null)
                input = conn.getInputStream();

            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                ret = getPostResult(html);

            }

        } catch (IOException e) {

        }
        return ret;
    }

    private String buildBody(String comment) {
        StringBuilder sb = new StringBuilder();
        sb.append("post_content=");

        sb.append(StringUtils.encodeUrl(comment, "GBK"));

        sb.append("&tid=");
        sb.append(tid);

        sb.append("&pid=");
        sb.append(pid);
        sb.append("&fid=");
        sb.append(fid);
        sb.append("&nojump=");
        sb.append("1");
        sb.append("&step=");
        sb.append("2");
        sb.append("&action=");
        sb.append("reply");
        sb.append("&comment=");
        sb.append("1");
        sb.append("&lite=");
        sb.append("htmljs");
        if (anonymode == 1) {
            sb.append("&anony=");
            sb.append("1");
        }

        return sb.toString();
    }

    protected String getPostResult(String html) {
        String js = StringUtils.getStringBetween(html, 0,
                "window.script_muti_get_var_store=", "</script>").result;
        if (StringUtils.isEmpty(js)) {
            return "未知错误";
        }
        try {
            JSONObject o = (JSONObject) JSON.parseObject(js);
            o = (JSONObject) o.get("data");
            o = (JSONObject) o.get("__MESSAGE");
            String result = o.getString("1");
            if (StringUtils.isEmpty(result)) {
                return "大概没权限,二哥滚粗";
            } else {
                if (o.getInteger("3") == 200) {
                    if (result.indexOf("发贴完毕") >= 0) {
                        success = true;
                    }
                    return result.replace("发贴完毕", "贴条成功").trim();
                } else {
                    return result;
                }
            }
        } catch (Exception e) {

        }
        return "未知错误";
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (success) {
            NLog.i("TSG", "DS");
        }
        notifier.OnPostCommentFinished(result, success);
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        super.onCancelled();
    }

}
