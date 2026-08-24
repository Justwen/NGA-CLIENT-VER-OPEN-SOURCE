package com.justwen.androidnga.base.network.retrofit;

import org.junit.Test;

import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RetrofitHelperTest {

    @Test
    public void addNgaHeadersUsesVersionedApiUserAgent() {
        Request original = new Request.Builder()
                .url("https://bbs.nga.cn/read.php?tid=1")
                .build();

        Request request = RetrofitHelper.addNgaHeaders(original, "session=secret", "custom-agent");

        assertEquals("session=secret", request.header("Cookie"));
        assertEquals("custom-agent", request.header("User-Agent"));
        assertEquals("Nga_Official/80024(Android12)", request.header("X-User-Agent"));
    }

    @Test
    public void safeLogMessageDoesNotContainHeadersOrQueryValues() {
        Request request = new Request.Builder()
                .url("https://bbs.nga.cn/read.php?tid=123&access_token=secret")
                .header("Cookie", "session=secret")
                .build();

        String message = RetrofitHelper.toSafeLogMessage(request);

        assertEquals("GET https://bbs.nga.cn/read.php", message);
        assertFalse(message.contains("secret"));
    }
}
