package sp.phone.mvp.model.convert;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sp.phone.mvp.model.entity.NotificationInfo;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import gov.anzong.androidnga.common.util.NLog;

public class ForumNotificationFactory {

    public static List<RecentReplyInfo> buildRecentReplyList(String content) {

        if (content.startsWith("window.script_muti_get_var_store=")) {
            content = content.substring("window.script_muti_get_var_store=".length());
        }

        List<RecentReplyInfo> infoList = new ArrayList<>();

        try {
            JSONObject obj = JSONObject.parseObject(content);
            obj = obj.getJSONObject("data").getJSONObject("0");
            JSONArray array = obj.getJSONArray("0");
            int unread = obj.getInteger("unread");
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    RecentReplyInfo info = buildRecentReplyInfo(array.getJSONObject(i));
                    info.setUnread(unread > 0);
                    infoList.add(info);
                }
            }
        } catch (Exception e) {
            NLog.e("buildRecentReplyList error" + e.getMessage());
        }
        return infoList;
    }

    private static RecentReplyInfo buildRecentReplyInfo(JSONObject obj) {

        String userName = obj.get("2").toString();
        String title = obj.get("5").toString();
        String tidStr = obj.get("6").toString();
        String pidStr;
        if (obj.containsKey("7")) {
            pidStr = obj.get("7").toString();
        } else {
            pidStr = obj.get("8").toString();
        }
        String timeStamp = obj.get("9").toString();

        String uid = obj.get("1").toString();
        int type = Integer.parseInt(obj.get("0").toString());
        RecentReplyInfo info = new RecentReplyInfo(type, userName, title, pidStr, tidStr);
        info.setTimeStamp(timeStamp);
        info.setUserId(uid);
        return info;

    }

    public static List<NotificationInfo> buildNotificationList(String content) {

        if (content.startsWith("window.script_muti_get_var_store=")) {
            content = content.substring("window.script_muti_get_var_store=".length());
        }

        List<NotificationInfo> infoList = new ArrayList<>();

        try {
            JSONObject obj = JSONObject.parseObject(content);
            obj = obj.getJSONObject("data").getJSONObject("0");
            if (obj != null) {
                int unread = obj.getInteger("unread");
                JSONArray array = obj.getJSONArray("0");
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        RecentReplyInfo info = buildRecentReplyInfo(array.getJSONObject(i));
                        info.setUnread(unread > 0);
                        infoList.add(info);
                    }
                }

                array = obj.getJSONArray("1");
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        NotificationInfo info = buildMessageInfo(array.getJSONObject(i));
                        info.setUnread(unread > 0);
                        infoList.add(info);
                    }
                }
            }

        } catch (Exception e) {
            NLog.e("buildNotificationList error " + e.getMessage());
        }
        return infoList;
    }

    private static NotificationInfo buildMessageInfo(JSONObject obj) {
        String userName = obj.get("2").toString();
        int type = Integer.parseInt(obj.get("0").toString());
        return new NotificationInfo(type, userName);
    }
}
