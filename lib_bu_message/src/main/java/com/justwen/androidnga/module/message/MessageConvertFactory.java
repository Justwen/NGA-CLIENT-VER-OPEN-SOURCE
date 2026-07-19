package com.justwen.androidnga.module.message;

import android.util.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.justwen.androidnga.core.data.MessageArticlePageInfo;
import com.justwen.androidnga.core.data.MessageDetailInfo;
import com.justwen.androidnga.core.data.MessageListInfo;
import com.justwen.androidnga.core.data.MessageThreadPageInfo;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.StringUtils;
import gov.anzong.androidnga.common.util.NLog;

/**
 * Created by Justwen on 2017/10/10.
 */

public class MessageConvertFactory {

    private String mErrorMsg = "";

    private static final String TAG = MessageConvertFactory.class.getSimpleName();

    public MessageListInfo getMessageListInfo(String js) {


        if (js == null || js.isEmpty()) {
            return null;
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
            NLog.e(TAG, "can not parse :\n" + js);
        }
        if (o == null) {
            try {
                o = (JSONObject) JSON.parseObject(js).get("error");
            } catch (Exception e) {
                NLog.e(TAG, "can not parse :\n" + js);
            }
            if (o == null) {
                mErrorMsg = "请重新登录";
            } else {
                mErrorMsg = o.getString("0");
                if (StringUtils.isEmpty(mErrorMsg))
                    mErrorMsg = "请重新登录";
            }
            return null;
        }

        MessageListInfo ret = new MessageListInfo();
        JSONObject o1 = (JSONObject) o.get("0");
        if (o1 == null) {
            mErrorMsg = "请重新登录";
            return null;
        }
        ret.set__nextPage(o1.getIntValue("nextPage"));
        ret.set__currentPage(o1.getIntValue("currentPage"));
        ret.set__rowsPerPage(o1.getIntValue("rowsPerPage"));


        List<MessageThreadPageInfo> messageEntryList = new ArrayList<>();
        JSONObject rowObj = (JSONObject) o1.get("0");
        for (int i = 1; rowObj != null; i++) {
            try {
                MessageThreadPageInfo entry = new MessageThreadPageInfo();
                entry.setMid(rowObj.getInteger("mid"));
                entry.setPosts(rowObj.getInteger("posts"));
                entry.setSubject(rowObj.getString("subject"));
                entry.setFrom_username(rowObj.getString("from_username"));
                entry.setLast_from_username(rowObj.getString("last_from_username"));
                int time = rowObj.getInteger("time");
                if (time > 0) {
                    entry.setTime(StringUtils.timeStamp2Date1(String.valueOf(time)));
                } else {
                    entry.setTime("");
                }
                time = rowObj.getIntValue("last_modify");
                if (time > 0) {
                    entry.setLastTime(StringUtils.timeStamp2Date1(String.valueOf(time)));
                } else {
                    entry.setLastTime("");
                }
                messageEntryList.add(entry);
                rowObj = (JSONObject) o1.get(String.valueOf(i));
            } catch (Exception e) {
                /*ThreadPageInfo entry = new ThreadPageInfo();
                String error = rowObj.getString("error");
				entry.setSubject(error);
				entry.setAuthor("");
				entry.setLastposter("");
				articleEntryList.add(entry);*/
            }
        }
        ret.setMessageEntryList(messageEntryList);
        return ret;
    }

    public MessageDetailInfo getMessageDetailInfo(String js, int page) {

        if (js == null) {
            mErrorMsg = ContextUtils.getString(R.string.network_error);
            return null;
        }
        js = js.replaceAll("window.script_muti_get_var_store=", "");
        if (js.indexOf("/*error fill content") > 0)
            js = js.substring(0, js.indexOf("/*error fill content"));
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
        js = js.replaceAll("\\[img\\]./mon_", "[img]http://img6.nga.178.com/attachments/mon_");

        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
        }
        if (o == null) {

            try {
                o = (JSONObject) JSON.parseObject(js).get("error");
            } catch (Exception e) {
                NLog.e(TAG, "can not parse :\n" + js);
            }
            if (o == null) {
                mErrorMsg = "请重新登录";
            } else {
                mErrorMsg = o.getString("0");
                if (StringUtils.isEmpty(mErrorMsg))
                    mErrorMsg = "请重新登录";
            }
            return null;
        }
        MessageDetailInfo ret = parseJsonThreadPage(js, page);
        return ret;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    /**
     * 解析页面内容
     *
     * @param js
     * @param page
     * @return
     */
    public MessageDetailInfo parseJsonThreadPage(String js, int page) {
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

        js = js.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
        js = js.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
        js = js.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");
        final String start = "\"__P\":{\"aid\":";
        final String end = "\"this_visit_rows\":";
        if (js.indexOf(start) != -1 && js.indexOf(end) != -1) {
            NLog.w(TAG, "here comes an invalid response");
            String validJs = js.substring(0, js.indexOf(start));
            validJs += js.substring(js.indexOf(end));
            js = validJs;

        }
        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
        }
        if (o == null)
            return null;

        MessageDetailInfo data = new MessageDetailInfo();

        JSONObject o1;
        o1 = (JSONObject) o.get("0");
        if (o1 == null)
            return null;

        JSONObject userInfoMap = (JSONObject) o1.get("userInfo");

        List<MessageArticlePageInfo> messageEntryList = convertJSobjToList(o1, userInfoMap, page);
        if (messageEntryList == null)
            return null;
        data.setMessageEntryList(messageEntryList);
        data.set__currentPage(o1.getIntValue("currentPage"));
        data.set__nextPage(o1.getIntValue("nextPage"));
        String alluser = o1.getString("allUsers"), allusertmp = "";
        alluser = alluser.replaceAll("	", " ");
        String alluserarray[] = alluser.split(" ");
        for (int i = 1; i < alluserarray.length; i += 2) {
            allusertmp += alluserarray[i] + ",";
        }
        if (allusertmp.length() > 0)
            allusertmp = allusertmp.substring(0, allusertmp.length() - 1);
        data.set_Alluser(allusertmp);
        if (data.getMessageEntryList().get(0) != null) {
            String title = data.getMessageEntryList().get(0).getSubject();
            if (!StringUtils.isEmpty(title)) {
                data.set_Title(title);
            } else {
                data.set_Title("");
            }
        }
        return data;

    }

    private List<MessageArticlePageInfo> convertJSobjToList(JSONObject rowMap, JSONObject userInfoMap, int page) {
        List<MessageArticlePageInfo> __R = new ArrayList<MessageArticlePageInfo>();
        if (rowMap == null)
            return null;
        rowMap = (JSONObject) rowMap.get("allmsgs");
        JSONObject rowObj = (JSONObject) rowMap.get("0");
        for (int i = 1; rowObj != null; i++) {
            MessageArticlePageInfo row = new MessageArticlePageInfo();

            row.setContent(rowObj.getString("content"));
            row.setLou(20 * (page - 1) + i);
            row.setSubject(rowObj.getString("subject"));
            int time = rowObj.getIntValue("time");
            if (time > 0) {
                row.setTime(StringUtils.timeStamp2Date1(String.valueOf(time)));
            } else {
                row.setTime("");
            }
            row.setFrom(rowObj.getString("from"));
            fillUserInfo(row, userInfoMap);
            formatContent(row);
            __R.add(row);
            rowObj = (JSONObject) rowMap.get(String.valueOf(i));
        }
        return __R;
    }

    private static void formatContent(MessageArticlePageInfo row) {

        String content = row.getContent();
        content = StringUtils.replaceAll(content, "\\[quote\\](.+?)\\[/quote\\]", "\n$1\n").replaceAll("<br/>", "\n");
        row.setContent(content);

        List<Pair<String, Boolean>> contentSections = row.getContentSections();
        for (String section : row.getContent().split("\\[url]")) {
            if (section.contains("[/url]")) {
                String[] urlSection = section.split("\\[/url]");
                contentSections.add(new Pair<>(urlSection[0], Boolean.TRUE));
                if (urlSection.length > 1) {
                    contentSections.add(new Pair<>(urlSection[1], Boolean.FALSE));
                }
            } else {
                contentSections.add(new Pair<>(section, Boolean.FALSE));
            }
        }
        row.setContentSections(contentSections);
    }

    private void fillUserInfo(MessageArticlePageInfo row, JSONObject userInfoMap) {
        JSONObject userInfo = (JSONObject) userInfoMap.get(row.getFrom());
        if (userInfo == null) {
            return;
        }

        row.setAuthor(userInfo.getString("username"));
        row.setJs_escap_avatar(userInfo.getString("avatar"));
        row.setYz(userInfo.getString("yz"));
        row.setMute_time(userInfo.getString("mute_time"));
        row.setSignature(userInfo.getString("signature"));
    }

}
