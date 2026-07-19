package gov.anzong.androidnga.core.decode;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Map;

import gov.anzong.androidnga.base.util.StringUtils;
import gov.anzong.androidnga.core.corebuild.HtmlVoteBuilder;
import gov.anzong.androidnga.core.data.HtmlData;

public class ForumVoteDecoder implements IForumDecoder {

    @Override
    public String decode(String content, @Nullable HtmlData htmlData) {
        try {
            if (content.contains("game_")) {

                // 游戏评分
                // [randomblock]<br/>[fixsize height 52 width 50 90]
                content = StringUtils.replaceAll(content, "\\[randomblock]\\<br/>\\[fixsize height 52 width 50 90]", "<div class=\"fixblk\" style=\" clear: both; overflow: hidden; width: 100%;height:700px; box-shadow: rgb(0, 0, 0) 0px 0px 15px -8px inset; background: rgb(245, 232, 203); \"><br/><div style=\"margin: auto; overflow: hidden; position: relative; z-index: 0;height:52em; max-width: 90em; min-width: 28em; transform-origin: left top; transform: scale(0.496402, 0.496402);\">");
                content = StringUtils.replaceAll(content, "\\[/randomblock]", "</div></div>");

                // 小说评分
                // [randomblock]<br/>[fixsize height 10 width 30 90]
                content = StringUtils.replaceAll(content, "\\[randomblock]\\<br/>\\[fixsize height 10 width 30 90]", "<div class=\"fixblk\" style=\" clear: both; overflow: hidden; width: 511.667px; height: 171px; box-shadow: rgb(0, 0, 0) 0px 0px 15px -8px inset; background: rgb(245, 232, 203); \"><br/><div style=\"margin: auto; overflow: hidden; position: relative; z-index: 0;height:30em; max-width: 90em; min-width: 28em; transform-origin: left top; transform: scale(0.496402, 0.496402);\">");

                content = StringUtils.replaceAll(content, "\\[comment oth_title_alias]", "");
                content = StringUtils.replaceAll(content, "\\[/comment oth_title_alias]", "");

                content = StringUtils.replaceAll(content, "\\[comment oth_type_name]", "");
                content = StringUtils.replaceAll(content, "\\[/comment oth_type_name]", "");

                // [style float left margin 1 0 1 1 width 9 height 7 background #b22222 align center border-radius 0.3 font 0 #fff]
                content = StringUtils.replaceAll(content, "\\[style float left margin 1 0 1 1 width 9 height 7 background #b22222 align center border-radius 0.3 font 0 #fff]", "<div style=\"display:inline-block;float:left;margin:1em 0em 1em 1em;width:9em;height:7em;background:#b22222;text-align:center;border-radius:0.3em;color:#fff;\">");

                // [style font 4 line-height 1.3 innerHTML &#36;votedata_voteavgvalue]
                content = StringUtils.replaceAll(content, "\\[style font 4 line-height 1.3 innerHTML &#36;votedata_voteavgvalue]", "<div style=\"display:inline-block;font-size:4em;line-height:1.3em;\">&#36;votedata_voteavgvalue");
                // [style font 1 line-height 1.2]
                content = StringUtils.replaceAll(content, "\\[style font 1 line-height 1.2]", "<div style=\"display:inline-block;font-size:1em;line-height:1.2em;\">");

                // [style innerHTML &#36;votedata_usernum]
                content = StringUtils.replaceAll(content, "\\[style innerHTML &#36;votedata_usernum]", "<div style=\"display:inline-block;\">&#36;votedata_usernum");

                // [style float left margin 1 0 1 1 color #444]
                content = StringUtils.replaceAll(content, "\\[style float left margin 1 0 1 1 color #444]", "<div style=\"display:inline-block;float:left;margin:1em 0em 1em 1em;color:#444;\">");

                // [style align justify-all]
                content = StringUtils.replaceAll(content, "\\[style align justify-all]", "<div style=\"display:inline-block;text-align:justify;text-align-last:justify;text-justify:inter-word;\">");

                // [style font 3 #444 line-height 1 width 100%]
                content = StringUtils.replaceAll(content, "\\[style font 3 #444 line-height 1 width 100%]", "<div style=\"display:inline-block;font-size:3em;color:#444;line-height:1em;width:100%;\">");

                content = StringUtils.replaceAll(content, "\\[style width 100% line-height 2.5]", "<div style=\"display:inline-block;width:100%;line-height:2.5em;\">");

                content = StringUtils.replaceAll(content, "\\[style line-height 1.5]", "<div style=\"display:inline-block;line-height:1.5em;\">");

                content = StringUtils.replaceAll(content, "\\[style color #fff padding 0 0.5 background #0c7da8 border-radius 0.2]", "<div style=\"display:inline-block;color:#fff;padding:0em 0.5em;background:#0c7da8;border-radius:0.2em;\">");

                content = StringUtils.replaceAll(content, "\\[style color #444 margin 0 1 1 1 float left clear both]", "<div style=\"display:inline-block;color:#444;margin:0em 1em 1em 1em;float:left;clear:both;\">");

                content = StringUtils.replaceAll(content, "\\[comment game_title_image]\\[style border-radius 0.3 width 50 src .", "<img src=\"https://img.nga.178.com/attachments");

                content = StringUtils.replaceAll(content, "]\\[/style]\\[/comment game_title_image]", "\" style=\"display:inline-block;border-radius:0.3em;width:50em;\">");

                content = StringUtils.replaceAll(content, "\\[comment game_title_cn]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_title_cn]", "");

                content = StringUtils.replaceAll(content, "\\[comment oth_title_cn]", "");
                content = StringUtils.replaceAll(content, "\\[/comment oth_title_cn]", "");

                content = StringUtils.replaceAll(content, "\\[comment game_title]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_title]", "");

                content = StringUtils.replaceAll(content, "\\[style float left width 20]", "<div style=\"display:inline-block;float:left;width:20em;\">");

                content = StringUtils.replaceAll(content, "\\[style float left]", "<div style=\"display:inline-block;float:left;\">");
                content = StringUtils.replaceAll(content, "\\[style font 2 line-height 1.5]", "<div style=\"display:inline-block;font-size:2em;line-height:1.5em;\">");

                content = StringUtils.replaceAll(content, "\\[comment game_release]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_release]", "");

                content = StringUtils.replaceAll(content, "\\[style float left clear both]", "<div style=\"display:inline-block;float:left;clear:both;\">");

                content = StringUtils.replaceAll(content, "\\[comment game_website]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_website]", "");

                content = StringUtils.replaceAll(content, "\\[style font 2 #b22222 line-height 1.5]", "<div style=\"display:inline-block;font-size:2em;color:#b22222;line-height:1.5em;\">");


                content = StringUtils.replaceAll(content, "\\[symbol link]", "");
                content = StringUtils.replaceAll(content, "\\[stripbr]", "");
                content = StringUtils.replaceAll(content, "\\[comment game_type]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_type]", "");
                content = StringUtils.replaceAll(content, "\\[comment game_publisher]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_publisher]", "");
                content = StringUtils.replaceAll(content, "\\[comment game_devloper]", "");
                content = StringUtils.replaceAll(content, "\\[/comment game_devloper]", "");
                content = StringUtils.replaceAll(content, "\\[/style]\\<br/>\\<br/>\\<br/>", "</div><br/>");
                content = StringUtils.replaceAll(content, "\\[/style]\\<br/>\\<br/>", "</div><br/>");
                // [/style]
                content = StringUtils.replaceAll(content, "\\[/style]", "</div>");
            }

            if (!TextUtils.isEmpty(htmlData.getVote())) {
                Map<String, String> voteMap = HtmlVoteBuilder.genVoteMap(htmlData);
                for (Map.Entry<String, String> entry : voteMap.entrySet()) {
                    String key = entry.getKey();
                    String voteType = voteMap.get("type");
                    if (voteType != null && HtmlVoteBuilder.isInteger(key) && voteType.equals("2")) {
                        String[] voteInfo = HtmlVoteBuilder.getVoteScore(voteMap, key);
                        content = StringUtils.replaceAll(content, "&#36;votedata_voteavgvalue", voteInfo[0]);
                        content = StringUtils.replaceAll(content, "&#36;votedata_usernum", voteInfo[1]);
                    }
                }
            }

        } catch (Exception e) {
            // ignore
        }
        return content;
    }
}
