package sp.phone.util;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.util.EmoticonUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.http.bean.StringFindResult;
import sp.phone.theme.ThemeManager;

@SuppressLint("SimpleDateFormat")
public class StringUtils {
    public final static String key = "asdfasdf";
    private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
    private static final String styleAlignRight = "<div style='text-align:right' >";
    private static final String styleAlignLeft = "<div style='text-align:left' >";
    private static final String styleAlignCenter = "<div style='text-align:center' >";
    private static final String styleColor = "<span style='color:$1' >";
    private static final String ignoreCaseTag = "(?i)";
    private static final String endDiv = "</div>";

    private static final String[] SAYING = ContextUtils.getResources().getStringArray(R.array.saying);

    /**
     * 验证是否是邮箱
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email))
            return false;
        String pattern1 = "^([a-z0-9A-Z]+[-_|\\.]?)+[a-z0-9A-Z_]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(pattern1);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否是 "" 或者 null
     */
    public static boolean isEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    /* 给候总客户端乱码加适配 */
    public static String unescape(String src) {
        if (isEmpty(src))
            return "";
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        String patternStr = "[A-Fa-f0-9]{4}";
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (pos > src.length() - 3) {
                    tmp.append(src.substring(pos, src.length()));
                    lastPos = pos + 3;
                } else {
                    if (src.charAt(pos + 1) == 'u') {
                        try {
                            if (Pattern.matches(patternStr,
                                    src.substring(pos + 2, pos + 6))) {
                                ch = (char) Integer.parseInt(
                                        src.substring(pos + 2, pos + 6), 16);
                                tmp.append(ch);
                                lastPos = pos + 6;
                            } else {
                                tmp.append(src.substring(pos, pos + 3));
                                lastPos = pos + 3;
                            }
                        } catch (Exception e) {
                            tmp.append(src.substring(pos, pos + 3));
                            lastPos = pos + 3;
                        }

                    } else {
                        try {
                            ch = (char) Integer.parseInt(
                                    src.substring(pos + 1, pos + 3), 16);
                            tmp.append(ch);
                            lastPos = pos + 3;
                        } catch (Exception e) {
                            tmp.append(src.substring(pos, pos + 3));
                            lastPos = pos + 3;
                        }
                    }
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * yy-M-dd hh:mm
     */
    public static Long sDateToLong(String sDate) {
        DateFormat df = new SimpleDateFormat("yy-M-dd hh:mm");
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static Long parseLong(String str) {
        if (str == null) {
            return null;
        } else {
            if (str.equals("")) {
                return 0l;
            } else {
                return Long.parseLong(str);
            }
        }
    }

    public static Long sDateToLong(String sDate, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String encodeUrl(final String s, final String charset) {

        /*
         * try { return java.net.URLEncoder.encode(s,charset); // this not work
         * in android 4.4 if a english char is followed //by a Chinese character
         *
         * } catch (UnsupportedEncodingException e) {
         *
         * return ""; }
         */
        String ret = UriEncoderWithCharset.encode(s, null, charset);
        // NLog.i("111111", s+"----->"+ret);
        return ret;
    }

    public static String parseHTML(String s) {
        // 转换字体
        if (s.indexOf("[quote]") != -1) {
            s = s.replace("[quote]", "");
            s = s.replace("[/quote]", "</font><font color='#1d2a63' size='10'>");

            s = s.replace("[b]", "<font color='red' size='1'>");
            s = s.replace("[/b]", "</font>");
            s = s.replace("<br/><br/>", "<br/>");
            s = s.replace("<br/><br/>", "<br/>");

            s = s.replace("[/pid]", "<font color='blue' size='2'>");
            s = s + "</font>";
        } else {
            s = "<font color='#1d2a63' size='10'>" + s;
            s = s + "</font>";
        }
        // 转换 表情

        s = s.replaceAll("(\\[s:\\d\\])", "<img src='$1'>");
        return s;
    }

    public static String decodealbum(String s, String quotediv) {
        int startpos = s.indexOf("[album="), endpos = s.indexOf("[/album]") + 8;
        String sup = "", sdown = "", salbum = "", stemp = "", stitle = "";
        while (startpos < endpos && startpos >= 0) {
            sup = s.substring(0, startpos);
            if (endpos >= 0)
                sdown = s.substring(endpos, s.length());
            salbum = s.substring(startpos, endpos);
            stitle = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$1");
            stemp = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$2");
            if (stemp.startsWith("<br/>")) {
                stemp = "[img]" + stemp.substring(5) + "[/img]";
            }
            stitle = "相册列表:" + stitle + "<br/>";
            stemp = stemp.replaceAll("<br/>", "[/img]<br/><br/>[img]");
            stemp = "<br/>" + quotediv + stitle + "<br/>" + stemp + "</div>";
            s = sup + stemp + sdown;
            startpos = s.indexOf("[album=");
            endpos = s.indexOf("[/album]") + 8;
        }
        return s;
    }

    public static String decodeForumTag(String ret, boolean showImage,
                                        int imageQuality, @Nullable List<String> imageUrls) {
        if (StringUtils.isEmpty(ret))
            return "";
        // s = StringUtils.unEscapeHtml(s);
        String quoteStyle = "<div style='background:#E8E8E8;padding:5px;border:1px solid #888' >";
        if (ThemeManager.getInstance().isNightMode())
            quoteStyle = "<div style='background:#000000;padding:5px;border:1px solid #888' >";

        final String styleLeft = "<div style='float:left' >";
        final String styleRight = "<div style='float:right' >";
        ret = decodealbum(ret, quoteStyle);
        ret = ret.replaceAll(ignoreCaseTag + "&amp;", "&");
        ret = ret.replaceAll(ignoreCaseTag + "\\[l\\]", styleLeft);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/l\\]", endDiv);
        // ret = ret.replaceAll("\\[L\\]", styleLeft);
        // ret = ret.replaceAll("\\[/L\\]", endDiv);

        ret = ret.replaceAll(ignoreCaseTag + "\\[r\\]", styleRight);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/r\\]", endDiv);
        // ret = ret.replaceAll("\\[R\\]", styleRight);
        // ret = ret.replaceAll("\\[/R\\]", endDiv);

        ret = ret.replaceAll(ignoreCaseTag + "\\[align=right\\]", styleAlignRight);
        ret = ret.replaceAll(ignoreCaseTag + "\\[align=left\\]", styleAlignLeft);
        ret = ret.replaceAll(ignoreCaseTag + "\\[align=center\\]", styleAlignCenter);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/align\\]", endDiv);

        ret = ret.replaceAll(
                ignoreCaseTag
                        + "\\[b\\]Reply to \\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\] (.+?)\\[/b\\]",
                "[quote]Reply to [b]<a href='" + Utils.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;'>[Reply]</a> $4[/b][/quote]");

        ret = ret.replaceAll(
                ignoreCaseTag + "\\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;'>[Reply]</a>");

        // 某些帖子会导致这个方法卡住, 暂时不清楚原因, 和这个方法的作用.... by elrond
        /*ret = ret.replaceAll(
                ignoreCaseTag + "={3,}((^=){0,}(.*?){0,}(^=){0,})={3,}",
                "<h4 style='font-weight: bold;border-bottom: 1px solid #AAA;clear: both;margin-bottom: 0px;'>$1</h4>");*/

        ret = ret.replaceAll(ignoreCaseTag + "\\[quote\\]", quoteStyle);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/quote\\]", endDiv);

        ret = ret.replaceAll(ignoreCaseTag + "\\[code\\]", quoteStyle + "Code:");
        ret = ret.replaceAll(ignoreCaseTag + "\\[code(.+?)\\]", quoteStyle);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/code\\]", endDiv);
        // reply
        // ret = ret.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
        // ret = ret.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+,\\d+,\\d\\]Reply\\[/pid\\]", "Reply");

        // topic
        ret = ret.replaceAll(ignoreCaseTag + "\\[tid=\\d+\\]Topic\\[/pid\\]",
                "Topic");
        ret = ret.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\]Topic\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[Topic]</a>");
        // reply
        // s =
        // s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
        // , "Reply to Reply <b>$1</b>");
        // 转换 tag
        // [b]
        ret = ret.replaceAll(ignoreCaseTag + "\\[b\\]", "<b>");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/b\\]", "</b>"/* "</font>" */);

        // item
        ret = ret.replaceAll(ignoreCaseTag + "\\[item\\]", "<b>");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/item\\]", "</b>");

        ret = ret.replaceAll(ignoreCaseTag + "\\[u\\]", "<u>");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/u\\]", "</u>");

        ret = ret.replaceAll(ignoreCaseTag + "\\[s:(\\d+)\\]",
                "<img src='file:///android_asset/a$1.gif'>");
        ret = buildEmoticonImage(ret);
        ret = ret.replace(ignoreCaseTag + "<br/><br/>", "<br/>");
        // [url][/url]
        ret = ret.replaceAll(
                ignoreCaseTag + "\\[url\\]/([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"" + Utils.getNGAHost() + "$1\">" + Utils.getNGAHost() + "$1</a>");
        ret = ret.replaceAll(
                ignoreCaseTag + "\\[url\\]([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"$1\">$1</a>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[url=/([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"" + Utils.getNGAHost() + "$1\">$2</a>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[url=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"$1\">$2</a>");
        ret = ret.replaceAll(ignoreCaseTag
                + "\\[uid=?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        ret = ret.replaceAll(
                ignoreCaseTag + "Post by\\s{0,}([^\\[\\s]{1,})\\s{0,}\\(",
                "Post by <a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[$1]</a> (");
        ret = ret.replaceAll(
                ignoreCaseTag + "\\[@(.{2,20}?)\\]",
                "<a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[@$1]</a>");
        ret = ret.replaceAll(ignoreCaseTag
                + "\\[uid=-?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[hip\\](.+?)\\[\\/hip\\]",
                "$1");
        ret = ret.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\](.+?)\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[$2]</a>");
        ret = ret.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\]\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[Reply]</a>");
        ret = ret.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\](.+?)\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[$2]</a>");
        // flash
        ret = ret.replaceAll(
                ignoreCaseTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
                "<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
        // color

        // ret = ret.replaceAll("\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
        // ,"<b style=\"color:$1\">$2</b>");
        ret = ret.replaceAll(ignoreCaseTag + "\\[color=([^\\[|\\]]+)\\]",
                styleColor);
        ret = ret.replaceAll(ignoreCaseTag + "\\[/color\\]", "</span>");

        // lessernuke
        ret = ret.replaceAll("\\[lessernuke\\]", lesserNukeStyle);
        ret = ret.replaceAll("\\[/lessernuke\\]", endDiv);

        ret = ret.replaceAll(
                "\\[table\\]",
                "<div><table cellspacing='0px' style='border:1px solid #aaa;width:99.9%;'><tbody>");
        ret = ret.replaceAll("\\[/table\\]", "</tbody></table></div>");
        ret = ret.replaceAll("\\[tr\\]", "<tr>");
        ret = ret.replaceAll("\\[/tr\\]", "<tr>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td(\\d+)\\]",
                "<td style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' rowspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td colspan='$1' rowspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' colspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$1' colspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$3' colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$2' colspan='$3'  style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");


        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan=(\\d+)\\]",
                "<td colspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan=(\\d+)\\]",
                "<td rowspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        ret = ret.replaceAll("\\[td\\]", "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        ret = ret.replaceAll("\\[/td\\]", "<td>");
        // [i][/i]
        ret = ret.replaceAll(ignoreCaseTag + "\\[i\\]",
                "<i style=\"font-style:italic\">");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/i\\]", "</i>");
        // [del][/del]
        ret = ret.replaceAll(ignoreCaseTag + "\\[del\\]", "<del class=\"gray\">");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/del\\]", "</del>");

        ret = ret.replaceAll(ignoreCaseTag + "\\[font=([^\\[|\\]]+)\\]",
                "<span style=\"font-family:$1\">");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/font\\]", "</span>");

        // size
        ret = ret.replaceAll(ignoreCaseTag + "\\[size=(\\d+)%\\]",
                "<span style=\"font-size:$1%;line-height:$1%\">");
        ret = ret.replaceAll(ignoreCaseTag + "\\[/size\\]", "</span>");

        // [img]./ddd.jpg[/img]
        // if(showImage){
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='http://" + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1'><img src='http://"
                        + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1' style= 'max-width:100%' ></a>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='$1'><img src='$1' style= 'max-width:100%' ></a>");

        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[list\\](.+?)\\[/list\\]",
                "<ul>$1</ul>");
        ret = ret.replaceAll(ignoreCaseTag
                        + "\\[\\*\\](.+?)<br/>",
                "<li>$1</li>");

        try {
            ret = buildImage(ret, showImage, imageUrls);
            ret = buildAudioHtml(ret);
            ret = buildVideoHtml(ret);
            ret = convertGifImage(ret);
        } catch (Exception e) {
        }
        return ret;
    }

    private static String buildImage(String content, boolean showImage, List<String> imageUrls) {
        Pattern p = Pattern
                .compile("<img src='(http\\S+)' style= 'max-width:100%' >");
        Matcher m = p.matcher(content);
        while (m.find()) {
            String s0 = m.group();
            String s1 = m.group(1);
            String path = EmoticonUtils.getPathByURI(s1);
            if (path != null) {

                String newImgBlock = "<img src='"
                        + "file:///android_asset/" + path
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
            } else if (!showImage) {
                path = "ic_offline_image.png";
                String newImgBlock = "<img src='"
                        + "file:///android_asset/" + path
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
            } else {

                String newImgBlock = "<img src='"
                        + s1
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
                int t = s1.indexOf(HttpUtil.NGA_ATTACHMENT_HOST);
                if (t != -1 && imageUrls != null) {
                    imageUrls.add(s1);
                }
            }
        }
        return content;
    }

    private static String convertGifImage(String content) {
        Pattern pattern = Pattern.compile("(http\\S+).gif.(.*?).jpg");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String s = matcher.group(0);
            content = content.replaceAll(s, s.substring(0, s.indexOf(".gif") + 4));
        }
        return content;
    }

    private static String buildEmoticonImage(String content) {
        final String acniangofubbcode[] = {"blink", "goodjob", "上", "中枪",
                "偷笑", "冷", "凌乱", "反对", "吓", "吻", "呆", "咦", "哦", "哭", "哭1",
                "哭笑", "哼", "喘", "喷", "嘲笑", "嘲笑1", "囧", "委屈", "心", "忧伤", "怒",
                "怕", "惊", "愁", "抓狂", "抠鼻", "擦汗", "无语", "晕", "汗", "瞎", "羞",
                "羡慕", "花痴", "茶", "衰", "计划通", "赞同", "闪光", "黑枪"};// (0-44)
        final String acniangappadd[] = {
                "ac0.png", "ac1.png", "ac2.png", "ac3.png", "ac4.png", "ac5.png", "ac6.png",
                "ac7.png", "ac8.png", "ac9.png", "ac10.png", "ac11.png", "ac12.png", "ac13.png",
                "ac14.png", "ac15.png", "ac16.png", "ac17.png", "ac18.png", "ac19.png",
                "ac20.png", "ac21.png", "ac22.png", "ac23.png", "ac24.png", "ac25.png",
                "ac26.png", "ac27.png", "ac28.png", "ac29.png", "ac30.png", "ac31.png",
                "ac32.png", "ac33.png", "ac34.png", "ac35.png", "ac36.png", "ac37.png",
                "ac38.png", "ac39.png", "ac40.png", "ac41.png", "ac42.png", "ac43.png",
                "ac44.png"
        };
        final String newacniangofubbcode[] = {
                "goodjob", "诶嘿", "偷笑", "怒", "笑",
                "那个…", "哦嗬嗬嗬", "舔", "鬼脸", "冷",
                "大哭", "哭", "恨", "中枪", "囧",
                "你看看你", "doge", "自戳双目", "偷吃", "冷笑",
                "壁咚", "不活了", "不明觉厉", "是在下输了", "你为猴这么",
                "干杯", "干杯2", "异议", "认真", "你已经死了",
                "你这种人…", "妮可妮可妮", "惊", "抢镜头", "yes",
                "有何贵干", "病娇", "lucky", "poi", "囧2",
                "威吓", "jojo立", "jojo立2", "jojo立3", "jojo立4",
                "jojo立5",};// (0-45)
        final String newacniangappadd[] = {"a2_02.png", "a2_05.png", "a2_03.png", "a2_04.png",
                "a2_07.png", "a2_08.png", "a2_09.png", "a2_10.png", "a2_14.png",
                "a2_16.png", "a2_15.png", "a2_17.png", "a2_21.png", "a2_23.png",
                "a2_24.png", "a2_25.png", "a2_27.png", "a2_28.png", "a2_30.png",
                "a2_31.png", "a2_32.png", "a2_33.png", "a2_36.png", "a2_51.png",
                "a2_53.png", "a2_54.png", "a2_55.png", "a2_47.png", "a2_48.png",
                "a2_45.png", "a2_49.png", "a2_18.png", "a2_19.png", "a2_52.png",
                "a2_26.png", "a2_11.png", "a2_12.png", "a2_13.png", "a2_20.png",
                "a2_22.png", "a2_42.png", "a2_37.png", "a2_38.png", "a2_39.png",
                "a2_41.png", "a2_40.png",// 0-45
        };
        final String ngniangBBCode[] = {
                "呲牙笑", "奸笑", "问号", "茶", "笑指", "燃尽", "晕", "扇笑", "寄", "别急",
                "doge", "丧", "汗", "叹气", "吃饼", "吃瓜", "吐舌", "哭", "喘", "心", "喷",
                "困", "大哭", "大惊", "害怕", "惊", "暴怒", "气愤", "热", "瓜不熟", "瞎", "色",
                "斜眼", "问号大"
        };
        final String ngniangAppAdd[] = {
                "ng_1.png", "ng_2.png", "ng_3.png", "ng_4.png", "ng_5.png", "ng_6.png",
                "ng_7.png", "ng_8.png", "ng_9.png", "ng_10.png", "ng_11.png", "ng_12.png",
                "ng_13.png", "ng_15.png", "ng_16.png", "ng_17.png", "ng_18.png", "ng_19.png",
                "ng_20.png", "ng_21.png", "ng_22.png", "ng_24.png", "ng_25.png", "ng_26.png",
                "ng_27.png", "ng_28.png", "ng_30.png", "ng_31.png", "ng_32.png", "ng_33.png",
                "ng_34.png", "ng_35.png", "ng_37.png", "ng_38.png"
        };
        final String penguinOfUBBCode[] = {
                "战斗力", "哈啤", "满分", "衰", "拒绝",
                "心", "严肃", "吃瓜", "嘣", "嘣2",
                "冻", "谢", "哭", "响指", "转身"
        };
        final String penguinAppAdd[] = {
                "pg01.png", "pg02.png", "pg03.png", "pg04.png", "pg05.png",
                "pg06.png", "pg07.png", "pg08.png", "pg09.png", "pg10.png",
                "pg11.png", "pg12.png", "pg13.png", "pg14.png", "pg15.png"
        };
        final String pstofubbcode[] = {"举手", "亲", "偷笑", "偷笑2", "偷笑3",
                "傻眼", "傻眼2", "兔子", "发光", "呆",
                "呆2", "呆3", "呕", "呵欠", "哭",
                "哭2", "哭3", "嘲笑", "基", "宅",
                "安慰", "幸福", "开心", "开心2", "开心3",
                "怀疑", "怒", "怒2", "怨", "惊吓",
                "惊吓2", "惊呆", "惊呆2", "惊呆3", "惨",
                "斜眼", "晕", "汗", "泪", "泪2",
                "泪3", "泪4", "满足", "满足2", "火星",
                "牙疼", "电击", "看戏", "眼袋", "眼镜",
                "笑而不语", "紧张", "美味", "背", "脸红",
                "脸红2", "腐", "星星眼", "谢", "醉",
                "闷", "闷2", "音乐", "黑脸", "鼻血",};// (0-64)
        final String pstappadd[] = {"pt00.png", "pt01.png", "pt02.png", "pt03.png", "pt04.png",
                "pt05.png", "pt06.png", "pt07.png", "pt08.png", "pt09.png",
                "pt10.png", "pt11.png", "pt12.png", "pt13.png", "pt14.png",
                "pt15.png", "pt16.png", "pt17.png", "pt18.png", "pt19.png",
                "pt20.png", "pt21.png", "pt22.png", "pt23.png", "pt24.png",
                "pt25.png", "pt26.png", "pt27.png", "pt28.png", "pt29.png",
                "pt30.png", "pt31.png", "pt32.png", "pt33.png", "pt34.png",
                "pt35.png", "pt36.png", "pt37.png", "pt38.png", "pt39.png",
                "pt40.png", "pt41.png", "pt42.png", "pt43.png", "pt44.png",
                "pt45.png", "pt46.png", "pt47.png", "pt48.png", "pt49.png",
                "pt50.png", "pt51.png", "pt52.png", "pt53.png", "pt54.png",
                "pt55.png", "pt56.png", "pt57.png", "pt58.png", "pt59.png",
                "pt60.png", "pt61.png", "pt62.png", "pt63.png", "pt64.png",};

        final String dtofubbcode[] = {
                "ROLL", "上", "傲娇", "叉出去", "发光",
                "呵欠", "哭", "啃古头", "嘲笑", "心",
                "怒", "怒2", "怨", "惊", "惊2",
                "无语", "星星眼", "星星眼2", "晕", "注意",
                "注意2", "泪", "泪2", "烧", "笑",
                "笑2", "笑3", "脸红", "药", "衰",
                "鄙视", "闲", "黑脸",//0-32
        };
        final String dtappadd[] = {
                "dt01.png", "dt02.png", "dt03.png", "dt04.png", "dt05.png",
                "dt06.png", "dt07.png", "dt08.png", "dt09.png",
                "dt10.png", "dt11.png", "dt12.png", "dt13.png", "dt14.png",
                "dt15.png", "dt16.png", "dt17.png", "dt18.png", "dt19.png",
                "dt20.png", "dt21.png", "dt22.png", "dt23.png", "dt24.png",
                "dt25.png", "dt26.png", "dt27.png", "dt28.png", "dt29.png",
                "dt30.png", "dt31.png", "dt32.png", "dt33.png",//0-32
        };
        for (int i = 0; i < 45; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:ac:" + acniangofubbcode[i]
                    + "]", "<img src= file:///android_asset/ac/"
                    + acniangappadd[i] + "'>");
        }
        for (int i = 0; i < 46; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:a2:" + newacniangofubbcode[i]
                    + "]", "<img src='file:///android_asset/a2/"
                    + newacniangappadd[i] + "'>");
        }
        for (int i = 0; i < penguinOfUBBCode.length; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:ng:" + ngniangBBCode[i]
                    + "]", "<img src='file:///android_asset/ng/"
                    + ngniangAppAdd[i] + "' width=" + 60 + " height=" + 60 + ">");
        }
        for (int i = 0; i < penguinOfUBBCode.length; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:pg:" + penguinOfUBBCode[i]
                    + "]", "<img src='file:///android_asset/pg/"
                    + penguinAppAdd[i] + "' width=" + 60 + " height=" + 60 + ">");
        }
        for (int i = 0; i < 65; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:pst:" + pstofubbcode[i]
                    + "]", "<img src='file:///android_asset/pst/"
                    + pstappadd[i] + "'>");
        }
        for (int i = 0; i < 33; i++) {
            content = content.replaceAll(ignoreCaseTag + "\\[s:dt:" + dtofubbcode[i]
                    + "]", "<img src='file:///android_asset/dt/"
                    + dtappadd[i] + "'>");
        }
        return content;
    }

    private static String buildAudioHtml(String content) {
        String regex = "\\[flash=audio](.*?)\\[/flash]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String audioUrl = matcher.group();
            audioUrl = audioUrl.substring(14, audioUrl.indexOf("[/flash]") - 1);
            // <audio src="http://img.ngacn.cc/attachments/mon_201802/25/-7Q5-ak1cKe.mp3?duration=3&filename=nga_audio.mp3" controls="controls"></audio>
            audioUrl = "<audio src=\"http://img.ngacn.cc/attachments" + audioUrl + "&filename=nga_audio.mp3\" controls=\"controls\"></audio>";
            content = matcher.replaceFirst(audioUrl);
            matcher = pattern.matcher(content);
        }
        return content;
    }

    private static String buildVideoHtml(String content) {
        String regex = "\\[flash=video](.*?)\\[/flash]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            url = url.substring("[flash=video]".length() + 1, url.indexOf("[/flash]"));
            url = "<video src=\"http://img.ngacn.cc/attachments" + url + "\" controls=\"controls\"></video>";
            content = matcher.replaceFirst(url);
            matcher = pattern.matcher(content);
        }
        return content;
    }

    public static String removeBrTag(String s) {
        s = s.replaceAll("<br/><br/>", "\n");
        s = s.replaceAll("<br/>", "\n");
        return s;
    }

    public static String getSaying() {
        Random random = new Random();
        int num = random.nextInt(SAYING.length);
        return SAYING[num];
    }

    public static String unEscapeHtml(String s) {
        return StringHelper.unescapeHTML(s);
    }

    public static StringFindResult getStringBetween(String data, int begPosition, String startStr, String endStr) {
        StringFindResult ret = new StringFindResult();
        do {
            if (isEmpty(data) || begPosition < 0
                    || data.length() <= begPosition || isEmpty(startStr)
                    || isEmpty(startStr))
                break;

            int start = data.indexOf(startStr, begPosition);
            if (start == -1)
                break;

            start += startStr.length();
            int end = data.indexOf(endStr, start);
            if (end == -1)
                end = data.length();
            ret.result = data.substring(start, end);
            ret.position = end + endStr.length();

        } while (false);

        return ret;
    }

    public static String toBinaryArray(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++) {
            builder.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }
        return builder.toString();
    }

    public static int getUrlParameter(String url, String paraName) {
        if (StringUtils.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            NLog.e("getUrlParameter", "invalid url:" + url);
        }

        return ret;
    }

    public static String timeStamp2Date1(String timeStamp) {
        return timeStamp2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timeStamp2Date2(String timeStamp) {
        return timeStamp2Date(timeStamp, "MM-dd HH:mm");
    }

    public static String timeStamp2Date(String timeStamp, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp) * 1000);
        return new SimpleDateFormat(format, Locale.getDefault()).format(calendar.getTime());
    }

    public static String getStringFromAssets(String path) {
        AssetManager assetManager = ContextUtils.getContext().getAssets();
        try (InputStream is = assetManager.open(path)) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}