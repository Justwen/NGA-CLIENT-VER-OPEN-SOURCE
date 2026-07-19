package sp.phone.proxy;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.param.HttpPostClient;
import sp.phone.util.ActivityUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

public final class ProxyBridge {

    Context context;
    Toast toast;

    public ProxyBridge(Context ccontext, Toast mtoast) {
        // TODO Auto-generated constructor stub
        context = ccontext;
        toast = mtoast;
    }

    @JavascriptInterface
    public void postURL(String url) {
        ActivityUtils.getInstance().noticeSaying("正在提交...", context);
        (new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPostExecute(String result) {
                ActivityUtils.getInstance().dismiss();
                if (StringUtils.isEmpty(result))
                    result = "未知错误,请重试";
                if (result.startsWith("操作成功"))
                    result = "操作成功";
                if (toast != null) {
                    toast.setText(result);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(context,
                            result,
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                if (StringUtils.isEmpty(params[0]))
                    return "选择错误";
                String url = Utils.getNGAHost() + "nuke.php?" + params[0];
                HttpPostClient c = new HttpPostClient(url);
                String cookie = PhoneConfiguration.getInstance().getCookie();
                c.setCookie(cookie);
                try {
                    InputStream input = null;
                    HttpURLConnection conn = c.post_body(params[0]);
                    if (conn != null) {
                        if (conn.getResponseCode() >= 500) {
                            input = null;
                        } else {
                            if (conn.getResponseCode() >= 400) {
                                input = conn.getErrorStream();
                            } else
                                input = conn.getInputStream();
                        }
                    } else {
                        return "网络错误";
                    }

                    if (input != null) {
                        String js = IOUtils.toString(input, "gbk");
                        if (null == js) {
                            return context.getString(R.string.network_error);
                        }
                        js = js.replaceAll("window.script_muti_get_var_store=", "");
                        JSONObject o = null, oerror = null;
                        try {
                            o = (JSONObject) JSON.parseObject(js).get("data");
                            oerror = (JSONObject) JSON.parseObject(js).get("error");
                        } catch (Exception e) {
                            NLog.e("ProxyBridge", "can not parse :\n" + js);
                        }
                        if (o == null) {
                            if (oerror == null) {
                                return "请重新登录";
                            } else {
                                if (!StringUtils.isEmpty(oerror.getString("0"))) {
                                    return oerror.getString("0");
                                } else {
                                    return "二哥又开始乱搞了";
                                }
                            }
                        } else {
                            if (!StringUtils.isEmpty(o.getString("0"))) {
                                return o.getString("0");
                            } else {
                                return "二哥又开始乱搞了";
                            }
                        }
                    } else {
                        return "二哥在用服务器下毛片";
                    }
                } catch (IOException e) {
                }
                return "";
            }
        }).execute(url);
    }

}
