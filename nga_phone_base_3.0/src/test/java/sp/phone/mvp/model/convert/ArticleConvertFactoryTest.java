package sp.phone.mvp.model.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArticleConvertFactoryTest {

    @Test
    public void convertsOfficialAppApiResponseToLegacyShape() {
        String response = "{\"code\":0,\"msg\":\"\",\"totalPage\":2,"
                + "\"currentPage\":1,\"perPage\":20,\"vrows\":21,"
                + "\"attachPrefix\":\"img.nga.cn/attachments\","
                + "\"tsubject\":\"topic\",\"tauthorid\":7,\"tauthor\":\"owner\","
                + "\"fid\":-7,\"forum_name\":\"forum\",\"result\":[{"
                + "\"pid\":11,\"fid\":-7,\"tid\":22,\"type\":0,"
                + "\"postdate\":\"2026-08-14 12:00\",\"postdatetimestamp\":1,"
                + "\"subject\":\"topic\",\"content\":\"body\",\"from_client\":\"8\","
                + "\"lou\":0,\"vote_good\":3,\"vote_bad\":1,"
                + "\"attches\":[{\"aid\":\"9\",\"attachurl\":\"a.jpg\",\"thumb\":\"1\"}],"
                + "\"author\":{\"uid\":7,\"username\":\"owner\",\"memberid\":39,"
                + "\"member\":\"member\",\"avatar\":\"avatar.jpg\"}}]}";

        String converted = ArticleConvertFactory.convertAppApiToLegacyJson(response);
        assertNotNull(converted);

        JSONObject data = JSON.parseObject(converted).getJSONObject("data");
        JSONObject row = data.getJSONObject("__R").getJSONObject("0");
        assertEquals(21, data.getIntValue("__ROWS"));
        assertEquals(22, data.getJSONObject("__T").getIntValue("tid"));
        assertEquals("img.nga.cn/attachments", data.getJSONObject("__GLOBAL")
                .getString("_ATTACH_BASE_VIEW"));
        assertEquals("owner", row.getString("author"));
        assertEquals(7, row.getIntValue("authorid"));
        assertEquals(3, row.getIntValue("score"));
        assertEquals("a.jpg", row.getJSONObject("attachs")
                .getJSONObject("9").getString("attachurl"));
        assertEquals("owner", data.getJSONObject("__U")
                .getJSONObject("7").getString("username"));
    }
}
