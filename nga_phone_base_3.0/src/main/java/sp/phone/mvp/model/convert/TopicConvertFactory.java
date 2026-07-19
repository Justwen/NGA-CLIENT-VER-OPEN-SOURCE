package sp.phone.mvp.model.convert;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gov.anzong.androidnga.activity.compose.filter.FilterManager;
import gov.anzong.androidnga.activity.compose.filter.FilterKeyword;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.User;
import sp.phone.http.bean.TopicListBean;
import sp.phone.mvp.model.entity.SubBoard;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import gov.anzong.androidnga.common.util.ForumUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/11/21.
 */

public class TopicConvertFactory {

    private static final String TAG = TopicConvertFactory.class.getSimpleName();

    public TopicListInfo getTopicListInfo(String js, int page) {

        if (js.startsWith("window.script_muti_get_var_store=")) {
            js = js.substring("window.script_muti_get_var_store=".length());
        }

        TopicListBean topicListBean = JSON.parseObject(js, TopicListBean.class);

        try {
            TopicListInfo listInfo = new TopicListInfo();
            convertSubBoard(listInfo, topicListBean);
            convertTopic(listInfo, topicListBean, page);
            listInfo.curTime = topicListBean.getTime();
            listInfo.setName(topicListBean.getData().get__F().name);
            sort(listInfo);
            filter(listInfo);
            return listInfo;
        } catch (NullPointerException e) {
            NLog.e(TAG, "can not parse :\n" + js);
            return null;
        }

    }

    private void filter(TopicListInfo data) {

//        // 低版本android 没有stream方法
//        // TODO: 如果第一页全部都是被屏蔽的，可能会认为加载失败
//        data.setThreadPageList(
//                data.getThreadPageList().stream().filter((ThreadPageInfo threadPageInfo) -> {
//                    return FilterKeywordsManagerImpl
//                            .getInstance()
//                            .getKeywords()
//                            .parallelStream()
//                            .noneMatch(filterKeyword -> {
//                                if (filterKeyword.isEnabled()) {
//                                    return threadPageInfo
//                                            .getSubject()
//                                            .contains(filterKeyword.getKeyword());
//                                } else {
//                                    return false;
//                                }
//                            });
//                }).collect(Collectors.toList())
//               );


        FilterManager.INSTANCE.filterTopic(data.getThreadPageList());

    }

    private void sort(TopicListInfo listInfo) {
        List<ThreadPageInfo> list = listInfo.getThreadPageList();
        if (PhoneConfiguration.getInstance().needSortByPostOrder()) {
            Collections.sort(list, new Comparator<ThreadPageInfo>() {
                @Override
                public int compare(ThreadPageInfo o1, ThreadPageInfo o2) {
                    return o1.getPostDate() < o2.getPostDate() ? 1 : -1;
                }
            });
        }

        List<SubBoard> subBoards = listInfo.getSubBoardList();
        if (!subBoards.isEmpty()) {
            Collections.sort(subBoards, new Comparator<SubBoard>() {
                @Override
                public int compare(SubBoard o1, SubBoard o2) {
                    return o1.getFid() < o2.getFid() ? 1 : -1;
                }
            });
        }
    }

    private void convertSubBoard(TopicListInfo listInfo, TopicListBean topicListBean) {
        try {
            String subForumsStr = String.valueOf(topicListBean.getData().get__F().getSub_forums());
            if (TextUtils.isEmpty(subForumsStr)) {
                return;
            }
            Map<String, Map<String, String>> subBoardMap = JSON.parseObject(subForumsStr, Map.class);
            for (String key : subBoardMap.keySet()) {
                Map<String, String> boardMap = subBoardMap.get(key);
                SubBoard board = new SubBoard();
                Object obj = boardMap.get("0");
                if (key.startsWith("t")) {
                    board.setStid(Integer.parseInt(obj.toString()));
                } else {
                    board.setFid(Integer.parseInt(obj.toString()));
                }

                // 有些子版块的fid的key是3，大部分都是1
                if (boardMap.containsKey("3")) {
                    obj = boardMap.get("3");
                    board.setTidStr(obj.toString());
                    board.setType(1);
                } else {
                    board.setType(0);
                }
                board.setParentFidStr(String.valueOf(topicListBean.getData().get__F().getFid()));
                board.setName(boardMap.get("1"));
                board.setDescription(boardMap.get("2"));
                if (boardMap.containsKey("4")) {
                    obj = boardMap.get("4");
                    board.setChecked(ForumUtils.isBoardSubscribed(Integer.parseInt(obj.toString())));
                } else {
                    board.setType(-1);
                    board.setChecked(true);
                }
                listInfo.addSubBoard(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertTopic(TopicListInfo listInfo, TopicListBean topicListBean, int page) {
        Map<String, TopicListBean.DataBean.TBean> map = topicListBean.getData().get__T();
        int count = 0;
        while (count < map.size()) {
            String key = String.valueOf(count);
            TopicListBean.DataBean.TBean tBean = map.get(key);
            if (tBean == null || filterTopic(listInfo, topicListBean, tBean)) {
                count++;
                continue;
            }
            ThreadPageInfo pageInfo = new ThreadPageInfo();
            String author = tBean.getAuthor();
            String authorId = tBean.getAuthorid();
            if (author.startsWith("#anony_")) {
                pageInfo.setAnonymity(true);
                pageInfo.setAuthor(getAnonymityName(tBean.getAuthor()));
            } else if (!TextUtils.isEmpty(authorId) && authorId.startsWith("#anony_")) {
                    pageInfo.setAnonymity(true);
                    pageInfo.setAuthor(getAnonymityName(authorId));
            } else {
                pageInfo.setAuthorId(Integer.parseInt(authorId));
                pageInfo.setAuthor(tBean.getAuthor());
            }
            pageInfo.setLastPoster(tBean.getLastposter());
            pageInfo.setSubject(tBean.getSubject());
            pageInfo.setReplies(tBean.getReplies());
            pageInfo.setType(tBean.getType());
            pageInfo.setTopicMisc(tBean.getTopic_misc());
            pageInfo.setTitleFont(tBean.getTitlefont());
            int tid = tBean.getTid();
            String tpcUrl = tBean.getTpcurl();
            if (tpcUrl != null && tpcUrl.contains("tid")) {
                tid = StringUtils.getUrlParameter(tpcUrl, "tid");
            }
            pageInfo.setTid(tid);
            pageInfo.setPage(page);
            TopicListBean.DataBean.TBean.PBean pBean = tBean.get__P();
            if (pBean != null) {
                pageInfo.setPid(pBean.getPid());
                ThreadPageInfo.ReplyInfo replyInfo = new ThreadPageInfo.ReplyInfo();
                replyInfo.setAuthorId(pBean.getAuthorid());
                replyInfo.setContent(pBean.getContent());
                replyInfo.setPostDate(String.valueOf(pBean.getPostdate()));
                replyInfo.setPidStr(String.valueOf(pBean.getPid()));
                replyInfo.setTidStr(String.valueOf(pageInfo.getTid()));
                replyInfo.setSubject(pageInfo.getSubject());
                pageInfo.setReplyInfo(replyInfo);
            }

            String parent = tBean.parent;
            if (!TextUtils.isEmpty(parent)) {
                try {
                    JSONObject obj = JSON.parseObject(parent);
                    pageInfo.setBoard(Objects.requireNonNull(obj.get("2")).toString());
                } catch (Exception e) {
                    // ignore
                }
            }

            pageInfo.setPostDate(tBean.getPostdate());

            Map<String, String> topicMiscVar = tBean.topic_misc_var;
            if (topicMiscVar != null && pageInfo.isMirrorBoard()) {
                Object obj = topicMiscVar.get("3");
                if (obj != null) {
                    pageInfo.setFid(Integer.parseInt(obj.toString()));
                }
            }


            listInfo.addThreadPage(pageInfo);
            count++;
        }
    }

    private boolean filterTopic(TopicListInfo listInfo, TopicListBean topicListBean, TopicListBean.DataBean.TBean tBean) {
        if (topicListBean.getData().get__F() != null
                && PhoneConfiguration.getInstance().needFilterSubBoard()
                && topicListBean.getData().get__F().getFid() == -7
                && tBean.getRecommend() > 9) {
//            if (tBean.getType() == 2097153) {
//                Board board = new Board();
//                board.setName(tBean.getSubject());
//                board.setUrl(String.valueOf(tBean.getTid()));
//                board.setChecked(false);
//                board.setCancelable(true);
//                listInfo.addSubBoard(board);
//            } else if (tBean.getType() == 2097152) {
//                Board board = new Board();
//                board.setName(tBean.getSubject());
//                board.setUrl(String.valueOf(tBean.getTid()));
//                board.setChecked(true);
//                board.setCancelable(true);
//                listInfo.addSubBoard(board);
//            }
            NLog.d("屏蔽固定的渣帖子 " + tBean.toString());
            return true;
        } else {
            return false;
        }

    }

    private String getAnonymityName(String author) {
        String prefix = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥";
        String suffix = "王李张刘陈杨黄吴赵周徐孙马朱胡林郭何高罗郑梁谢宋唐许邓冯韩曹曾彭萧蔡潘田董袁于余叶蒋杜苏魏程吕丁沈任姚卢傅钟姜崔谭廖范汪陆金石戴贾韦夏邱方侯邹熊孟秦白江阎薛尹段雷黎史龙陶贺顾毛郝龚邵万钱严赖覃洪武莫孔汤向常温康施文牛樊葛邢安齐易乔伍庞颜倪庄聂章鲁岳翟殷詹申欧耿关兰焦俞左柳甘祝包宁尚符舒阮柯纪梅童凌毕单季裴霍涂成苗谷盛曲翁冉骆蓝路游辛靳管柴蒙鲍华喻祁蒲房滕屈饶解牟艾尤阳时穆农司卓古吉缪简车项连芦麦褚娄窦戚岑景党宫费卜冷晏席卫米柏宗瞿桂全佟应臧闵苟邬边卞姬师和仇栾隋商刁沙荣巫寇桑郎甄丛仲虞敖巩明佘池查麻苑迟邝";

        StringBuilder sb = new StringBuilder();
        int i = 6;
        for (int j = 0; j < 6; j++) {
            int pos;
            if (j == 0 || j == 3) {
                pos = Integer.valueOf(author.substring(i + 1, i + 2), 16);
                if (pos >= prefix.length()) {
                    pos = prefix.length() - 1;
                } else if (pos < 0) {
                    pos = 0;
                }
                sb.append(prefix.charAt(pos));
            } else {
                pos = Integer.valueOf(author.substring(i, i + 2), 16);
                if (pos >= suffix.length()) {
                    pos = suffix.length() - 1;
                } else if (pos < 0) {
                    pos = 0;
                }
                sb.append(suffix.charAt(pos));
            }
            i += 2;
        }
        return sb.toString();
    }
//
//    public TopicListInfo getTopicListInfo(String js, String uri) {
//        if (js == null || js.isEmpty()) {
//            mErrorMsg = ContextUtils.getString(R.string.network_error);
//            return null;
//        }
//
//        boolean filter = false;
//        final String greatSeaUri = Utils.getNGAHost() + "thread.php?fid=-7&page=1&lite=js&noprefix";
//        if (greatSeaUri.equals(uri)) {
//            filter = true;
//        }
//        String page = StringUtils.getStringBetween(uri, 0, "page=", "&").result;
//        if (StringUtils.isEmpty(page)) {
//            page = "1";
//        }
//        NLog.d(js);
//        if (js.indexOf("/*error fill content") > 0)
//            js = js.substring(0, js.indexOf("/*error fill content"));
//        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
//        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subjava.lang.Stringject\":\"+$1\",");
//        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
//        JSONObject o = null;
//        try {
//            o = (JSONObject) JSON.parseObject(js).get("data");
//        } catch (Exception e) {
//            NLog.e(TAG, "can not parse :\n" + js);
//        }
//        if (o == null) {
//            mErrorMsg = "请重新登录";
//            return null;
//        }
//
//        TopicListInfo ret = new TopicListInfo();
//        /**
//         * 再见垃圾板开始
//         * **/
//        Object topiclistftmp = o.get("__F");
//        int topiclistfid = 0;
//        if (topiclistftmp instanceof JSONObject) {
//            JSONObject otmp = (JSONObject) topiclistftmp;
//            if (otmp.get("fid") != null) {
//                topiclistfid = (Integer) otmp.get("fid");
//            }
////			if(table!=null && otmp.get("extra_html")!=null){
////				ret.set__TABLE(Integer.parseInt(table)+1);
////			}
//        }/**
//         * 再见垃圾板结束
//         * **/
//
//
//        Object rows = o.get("__ROWS");
//
//        if (rows instanceof Integer) {
//            ret.set__ROWS((Integer) o.get("__ROWS"));
//        } else {
//            ret.set__ROWS(10000);
//        }
//
//        rows = o.get("__T__ROWS");
//        if (rows != null)
//            ret.set__T__ROWS((Integer) rows);
//        else {
//            String message = null;
//            Object tmp = o.get("__MESSAGE");
//            if (tmp instanceof String) {
//                message = (String) o.get("__MESSAGE");
//            } else if (tmp instanceof JSONObject) {
//                o = (JSONObject) tmp;
//                message = (String) o.get("1");
//                if (message == null) {
//                    message = (String) o.get("0");
//                }
//            } else {
//                mErrorMsg = "二哥玩坏了或者你需要重新登录";
//            }
//            if (message != null) {
//                int pos = message.indexOf("<a href=");
//                if (pos > 0) {
//                    message = message.substring(0, pos);
//                }
//                mErrorMsg = message.replace("<br/>", "");
//
//            }
//            if (mErrorMsg.trim().endsWith("中没有符合条件的结果")) {
////				 ret.set__TABLE(Integer.parseInt(table));
//                ret.set__SEARCHNORESULT(true);
//                return ret;
//            } else {
//                ret.set__SEARCHNORESULT(false);
////				 ret.set__TABLE(-999);//NOT SEARCHING IN TABLE X
//                return null;
//            }
//        }
//
//        Object forum = o.get("__SELECTED_FORUM");
//        if (forum instanceof Integer)
//            ret.set__SELECTED_FORUM((Integer) forum);
//        else
//            ret.set__SELECTED_FORUM(0);
//
//        JSONObject o1 = (JSONObject) o.get("__T");
//
//        if (o1 == null) {
//            mErrorMsg = "请重新登录";
//            return null;
//        }
//
//
//        List<ThreadPageInfo> articleEntryList = new ArrayList<ThreadPageInfo>();
//        String t1 = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥";
//        String t2 = "王李张刘陈杨黄吴赵周徐孙马朱胡林郭何高罗郑梁谢宋唐许邓冯韩曹曾彭萧蔡潘田董袁于余叶蒋杜苏魏程吕丁沈任姚卢傅钟姜崔谭廖范汪陆金石戴贾韦夏邱方侯邹熊孟秦白江阎薛尹段雷黎史龙陶贺顾毛郝龚邵万钱严赖覃洪武莫孔汤向常温康施文牛樊葛邢安齐易乔伍庞颜倪庄聂章鲁岳翟殷詹申欧耿关兰焦俞左柳甘祝包宁尚符舒阮柯纪梅童凌毕单季裴霍涂成苗谷盛曲翁冉骆蓝路游辛靳管柴蒙鲍华喻祁蒲房滕屈饶解牟艾尤阳时穆农司卓古吉缪简车项连芦麦褚娄窦戚岑景党宫费卜冷晏席卫米柏宗瞿桂全佟应臧闵苟邬边卞姬师和仇栾隋商刁沙荣巫寇桑郎甄丛仲虞敖巩明佘池查麻苑迟邝";
//        int fixedCount = 0;
//        for (int i = 0; i - fixedCount < ret.get__T__ROWS(); i++) {
//            JSONObject rowObj = (JSONObject) o1.get(String.valueOf(i));
//            try {
//                ThreadPageInfo entry = JSONObject.toJavaObject(rowObj, ThreadPageInfo.class);
//                JSONObject rowObj__P = (JSONObject) rowObj.get("__P");
//                String tidarray = "";
//                if (null != rowObj__P) {
//                    if (rowObj__P.getInteger("pid") > 0) {
//                        entry.setPid(rowObj__P.getInteger("pid"));
//                        if (entry.getTid() > 0) {
//                            tidarray = "tidarray=" + String.valueOf(entry.getTid()) + "_" + String.valueOf(entry.getPid()) + "&page=" + page;
//                        }
//                    } else {
//                        if (entry.getTid() > 0) {
//                            tidarray = "tidarray=" + String.valueOf(entry.getTid()) + "&page=" + page;
//                        }
//                    }
//                } else {
//                    if (entry.getTid() > 0) {
//                        tidarray = "tidarray=" + String.valueOf(entry.getTid()) + "&page=" + page;
//                    }
//                }
//                entry.setTidarray(tidarray);
//                if (rowObj.get("author").toString().length() == 39 && rowObj.get("author").toString().startsWith("#anony_")) {
//                    StringBuilder sb = new StringBuilder();
//                    String aname = rowObj.get("author").toString();
//                    int ia = 6;
//                    for (int j = 0; j < 6; j++) {
//                        int pos = 0;
//                        if (j == 0 || j == 3) {
//                            pos = Integer.valueOf(aname.substring(ia + 1, ia + 2), 16);
//                            sb.append(t1.charAt(pos));
//                        } else if (j < 6) {
//                            pos = Integer.valueOf(aname.substring(ia, ia + 2), 16);
//                            sb.append(t2.charAt(pos));
//                        }
//                        ia += 2;
//                    }
//                    entry.setAuthor(sb.toString());
//                }
//
//                if (rowObj.get("lastposter").toString().length() == 39 && rowObj.get("lastposter").toString().startsWith("#anony_")) {
//                    StringBuilder sb = new StringBuilder();
//                    String aname = rowObj.get("lastposter").toString();
//                    int ia = 6;
//                    for (int j = 0; j < 6; j++) {
//                        int pos = 0;
//                        if (j == 0 || j == 3) {
//                            pos = Integer.valueOf(aname.substring(ia + 1, ia + 2), 16);
//                            sb.append(t1.charAt(pos));
//                        } else if (j < 6) {
//                            pos = Integer.valueOf(aname.substring(ia, ia + 2), 16);
//                            sb.append(t2.charAt(pos));
//                        }
//                        ia += 2;
//                    }
//                    entry.setLastposter(sb.toString());
//                }
//                if (!StringUtils.isEmpty(rowObj.getString("topic_misc"))) {
//                    entry.setTopicMisc(rowObj.getString("topic_misc"));
//                }
//                if (rowObj.getIntValue("type") != 0) {
//                    entry.setType(rowObj.getIntValue("type"));
//                }
//                if (rowObj.get("author").toString().length() == 38 && rowObj.get("author").toString().startsWith("anony_")) {
//                    StringBuilder sb = new StringBuilder();
//                    String aname = rowObj.get("author").toString();
//                    int ia = 6;
//                    for (int j = 0; j < 6; j++) {
//                        int pos = 0;
//                        if (j == 0 || j == 3) {
//                            pos = Integer.valueOf(aname.substring(ia + 1, ia + 2), 16);
//                            sb.append(t1.charAt(pos));
//                        } else if (j < 6) {
//                            pos = Integer.valueOf(aname.substring(ia, ia + 2), 16);
//                            sb.append(t2.charAt(pos));
//                        }
//                        ia += 2;
//                    }
//                    entry.setAuthor(sb.toString());
//                }
//                if (PhoneConfiguration.getInstance().showStatic ||
//                        (StringUtils.isEmpty(entry.getTop_level()) && StringUtils.isEmpty(entry.getStatic_topic()))) {
//                    if (PhoneConfiguration.getInstance().showLajibankuai) {
//                        articleEntryList.add(entry);//显示的话，不用管直接加
//                    } else {
//                        if (topiclistfid == -7) {//不显示的话，判断是否不要垃圾版
//                            if (entry.getFid() == -7)
//                                articleEntryList.add(entry);
//                        } else {
//                            articleEntryList.add(entry);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                fixedCount++;
//                /*ThreadPageInfo entry = new ThreadPageInfo();
//                String error = rowObj.getString("error");
//				entry.setSubject(error);
//				entry.setAuthor("");
//				entry.setLastposter("");
//				articleEntryList.add(entry);*/
//            }
//        }
//        if (!PhoneConfiguration.getInstance().showStatic && filter) {
//
//            int j = articleEntryList.size() - 1;
//            while (j >= 0) {
//                ThreadPageInfo info = articleEntryList.get(j);
//                if (info == null) {
//                    break;
//                }
//                if (info.getRecommend() > 9) {
//                    articleEntryList.remove(j);
//                }
//                --j;
//            }
//
//        }
//        ret.set__T__ROWS(articleEntryList.size());
//        ret.setArticleEntryList(articleEntryList);
//        return ret;
//    }
}
