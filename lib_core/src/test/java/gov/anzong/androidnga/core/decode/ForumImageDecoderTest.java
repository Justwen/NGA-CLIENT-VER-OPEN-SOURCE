package gov.anzong.androidnga.core.decode;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.anzong.androidnga.base.util.ContextUtils;

public class ForumImageDecoderTest {

    @BeforeClass
    public static void setUpApplication() {
        ContextUtils.setApplication(new TestApplication());
    }

    @Test
    public void decodesRelativeAttachmentWithCurrentNgaImageHost() {
        String content = new ForumImageDecoder().decode("[img]./mon_2020/01/example.jpg[/img]");

        assertEquals("<a href='http://img.nga.cn/attachments/mon_2020/01/example.jpg'><img src='http://img.nga.cn/attachments/mon_2020/01/example.jpg'></a>", content);
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
