package gov.anzong.androidnga.core.corebuild;

import java.util.List;
import java.util.Locale;

import gov.anzong.androidnga.core.data.AttachmentData;
import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlAttachmentBuilder implements IHtmlBuild {


    private static StringBuilder buildAudioAttachment(StringBuilder ret, AttachmentData attachment) {
        String url = attachment.getAttachUrl();
        ret.append("<tr><td><a href='http://")
                .append(attachment.getAttachmentHost())
                .append("/attachments/")
                .append(url)
                .append("'>")
                .append("nga_audio.mp3</a>")
                .append("</td></tr>");
        return ret;
    }

    private static StringBuilder buildImageAttachment(StringBuilder ret, AttachmentData attachment, int index, List<String> imageUrls) {

        String attachUrl = "http://" + attachment.getAttachmentHost() + "/attachments/" + attachment.getAttachUrl();
        String attachUrlThumb = attachUrl;
        String indexStr = String.valueOf(index);
        if ("1".equals(attachment.getThumb())) {
            attachUrlThumb = attachUrlThumb + ".thumb.jpg";
        }
        ret.append("<tr><td>")
                .append(String.format("<button id='show%s' type='button' onclick='displayImg(%s,\"%s\")'>点击显示附件</button>", indexStr, indexStr, attachUrlThumb))
                .append(String.format("<a href=%s>", attachUrl))
                .append(String.format("<img style='max-width:100%%'; id='img%s'/>", indexStr))
                .append("</a></td></tr>");
        if (!imageUrls.contains(attachUrl)) {
            imageUrls.add(attachUrl);
        }

        return ret;
    }

    @Override
    public CharSequence build(HtmlData htmlData, List<String> images) {
        if (htmlData.getAttachmentList() == null || htmlData.getAttachmentList().isEmpty()) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("<br/><br/>附件<hr/><br/>");
        if (htmlData.isDarkMode()) {
            ret.append("<table style='border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:10'>");
        } else {
            ret.append("<table style='border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:10'>");
        }
        ret.append("<tbody>");
        int attachmentCount = 0;
        int imageAttachmentCount = 0;

        for (AttachmentData attach : htmlData.getAttachmentList()) {
            String attachUrl = attach.getAttachUrl();
            if (attachUrl.contains("mp3")) {
                ret = buildAudioAttachment(ret, attach);
            } else if (attachUrl != null && attachUrl.toLowerCase(Locale.US).contains("mp4")) {
                // 视频附件由帖子列表中的原生播放器渲染，避免 WebView 下载链接。
                continue;
            } else {
                imageAttachmentCount++;
                buildImageAttachment(ret, attach, imageAttachmentCount, images);
            }
            attachmentCount++;
        }

        if (imageAttachmentCount > 0) {
            ret.append("<script> function displayImg(a,b){ document.getElementById('img'+a).src=b; document.getElementById('show' + a).style.display='none'; } </script>");
        }
        ret.append("</tbody></table>");
        if (attachmentCount == 0) {
            return "";
        } else {
            return ret;
        }
    }
}
