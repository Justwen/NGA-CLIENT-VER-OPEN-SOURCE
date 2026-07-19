package sp.phone.util;


import android.graphics.Bitmap;
import android.os.Build;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import gov.anzong.androidnga.common.util.AppEnvironment;
import gov.anzong.androidnga.common.util.NLog;

public class HttpUtil {

    public static final String NGA_ATTACHMENT_HOST = "img.nga.178.com"; //img.ngacn.cc";
    public static final String Servlet_phone = "/servlet/PhoneServlet";
    public static final String Servlet_timer = "/servlet/TimerServlet";
    private static final String[] servers = {"https://nga.178.com", "https://bbs.ngacn.cc"};
    private static final String TAG = HttpUtil.class.getSimpleName();
    private static final String[] host_arr = {};
    public static String PATH = AppEnvironment.getExternalStoragePictureDirectory() + "/nga_cache";
    public static String PATH_AVATAR = PATH + "/nga_cache";

    public static String Server = "https://bbs.nga.cn";
    public static String NonameServer = "http://ngac.sinaapp.com/nganoname";
    public static String HOST = "";
    public static String HOST_PORT = "";
    //软件名/版本 (硬件信息; 操作系统信息)
    //AndroidNga/571 (Xiaomi MI 2S; Android 4.1.1)
    public static String MODEL = android.os.Build.MODEL.toUpperCase(Locale.US);
    public static String MANUFACTURER = android.os.Build.MANUFACTURER.toUpperCase(Locale.US);

    //	public static final String USER_AGENT = new StringBuilder().append("Nga_Official/").append(NgaClientApp.version).append("([Xiaomi MI 2S];Android").append(android.os.Build.VERSION.RELEASE).append(")").toString();
    //	public static final String USER_AGENT = new StringBuilder().append("Mozilla/5.0 (Linux; U; Android 2.3.3; zh-cn; SH12C Build/S4040) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1").toString();
    @SuppressWarnings("unused")
    public static void selectServer2() {
        for (String host : host_arr) {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(host).openConnection();
                conn.setConnectTimeout(6000);
                int result = conn.getResponseCode();
                String re = conn.getResponseMessage();
                if (result == HttpURLConnection.HTTP_OK) {
                    HOST = host;//
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            }

        }
    }

    public static void switchServer() {
        int i = 0;
        for (; i < servers.length; ++i) {
            if (Server.equals(servers[i]))
                break;
        }
        i = (i + 1) % servers.length;
        Server = servers[i];
    }

    public static void downImage(String uri, String fileName) {
        try {
            URL url = new URL(uri);
            File file = new File(fileName);

            FileUtils.copyURLToFile(url, file, 2000, 5000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            NLog.e(TAG, "failed to download img:" + uri + "," + e.getMessage());
        }
    }

    public static void downImage3(Bitmap bitmap, String fileName) {
        File f = new File(fileName);
        InputStream is = null;
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @SuppressWarnings("unused")
    private static void writeFile(URL url, String fileName) {
        try {
            FileUtils.copyURLToFile(url, new File(fileName), 4000, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static String getHtml(String uri, String cookie) {
        InputStream is = null;
        String machine = "";
        if (MODEL.indexOf(MANUFACTURER) >= 0) {
            machine = android.os.Build.MODEL;
        } else {
            machine = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        }
        if (machine.length() < 19) {
            machine = "[" + machine + "]";
        }
        final String USER_AGENT = new StringBuilder().append("Nga_Official/").append(573).append("(").append(machine).append(";Android").append(android.os.Build.VERSION.RELEASE).append(")").toString();
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (!StringUtils.isEmpty(cookie))
                conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Charset", "GBK");
            if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            } else {
                conn.setRequestProperty("Connection", "close");
            }
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.connect();
            if (conn.getResponseCode() == 200)
                is = conn.getInputStream();
            else
                is = conn.getErrorStream();
            if ("gzip".equals(conn.getHeaderField("Content-Encoding")))
                is = new GZIPInputStream(is);
            String encoding = getCharset(conn, "GBK");
            return IOUtils.toString(is, encoding);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    public static String getCharset(HttpURLConnection conn, String defaultValue) {
        if (conn == null)
            return defaultValue;
        String contentType = conn.getHeaderField("Content-Type");
        if (StringUtils.isEmpty(contentType))
            return defaultValue;
        String startTag = "charset=";
        String endTag = " ";
        int start = contentType.indexOf(startTag);
        if (-1 == start)
            return defaultValue;
        start += startTag.length();
        int end = contentType.indexOf(endTag, start);
        if (-1 == end)
            end = contentType.length();
        if (contentType.substring(start, end).equals("no")) {
            return "utf-8";
        }
        return contentType.substring(start, end);
    }
}
