package gov.anzong.androidnga.core;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.core.corebuild.HtmlAttachmentBuilder;
import gov.anzong.androidnga.core.data.AttachmentData;
import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumBasicDecoder;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @BeforeClass
    public static void setUpApplication() {
        ContextUtils.setApplication(new TestApplication());
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testQuote() {
        String testString = "[quote]123[quote]456[/quote]789[/quote]";
        ForumBasicDecoder decoder = new ForumBasicDecoder();
        String result = decoder.decode(testString, createHtmlData());
        result = decoder.decode(result, createHtmlData());
        System.out.println(result);
    }

    private HtmlData createHtmlData() {
        return new HtmlData("");
    }

    @Test
    public void testSignPost() {
        String sign = "测试";
        try {
            System.out.println(URLEncoder.encode(sign,"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void videoAttachmentIsHandledOutsideWebView() {
        AttachmentData attachment = new AttachmentData();
        attachment.setAttachUrl("20260817/test_video.MP4");
        attachment.setAttachmentHost("img.ngacn.cc");

        HtmlData data = createHtmlData();
        data.setAttachmentList(Collections.singletonList(attachment));

        CharSequence html = new HtmlAttachmentBuilder().build(data, new ArrayList<>());
        assertEquals("", html.toString());
    }

    @Test
    public void videoFlashTagIsNotShownAsPlainText() {
        String content = "[flash]./mon_202608/17/test.mp4[/flash]";
        assertEquals("", new ForumBasicDecoder().decode(content, createHtmlData()));
    }

    @Test
    public void directVideoFlashTagWithQueryIsHandledOutsideWebView() {
        String content = "[flash]https://img.example.com/test.mp4?duration=3[/flash]";
        assertEquals("", new ForumBasicDecoder().decode(content, createHtmlData()));
    }

    @Test
    public void weiboVideoFlashTagIsHandledOutsideWebView() {
        String content = "[flash]https://weibo.com/1638620395/ReRTVyZWl[/flash]";
        assertEquals("", new ForumBasicDecoder().decode(content, createHtmlData()));
    }

    @Test
    public void bilibiliVideoFlashTagIsHandledOutsideWebView() {
        String content = "[flash]https://www.bilibili.com/video/BV1X88Y6dEQB[/flash]";
        assertEquals("", new ForumBasicDecoder().decode(content, createHtmlData()));
    }

    private static class TestApplication extends Application {
        private final Resources resources = new TestResources();

        @Override
        public Resources getResources() {
            return resources;
        }

        @Override
        public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        }
    }

    private static class TestResources extends Resources {
        TestResources() {
            super(null, new DisplayMetrics(), new Configuration());
        }

        @Override
        public String[] getStringArray(int id) {
            return new String[]{"test"};
        }
    }
}
