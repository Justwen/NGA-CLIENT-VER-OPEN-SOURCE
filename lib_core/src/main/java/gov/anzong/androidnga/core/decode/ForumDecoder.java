package gov.anzong.androidnga.core.decode;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumDecoder {

    private static List<IForumDecoder> sDecoderPool = new ArrayList<>();

    static {
        sDecoderPool.add(new ForumBasicDecoder());
        sDecoderPool.add(new ForumVoteDecoder());
        sDecoderPool.add(new ForumAlbumDecoder());
        sDecoderPool.add(new ForumEmoticonDecoder());
        sDecoderPool.add(new ForumImageDecoder());
        sDecoderPool.add(new ForumDiceDecoder());
    }

    public static String decode(String rawData, @NonNull HtmlData htmlData, List<String> urls) {
        String content = rawData;
        for (IForumDecoder decoder : sDecoderPool) {
            content = decoder.decode(content, htmlData);
            if (urls != null && decoder.getImageUrls() != null && !decoder.getImageUrls().isEmpty()) {
                urls.addAll(decoder.getImageUrls());
            }
        }
        return content;
    }

    public static String decode(String rawData, @NonNull HtmlData htmlData) {
        return decode(rawData, htmlData, null);
    }

    public static String decodeBasic(String rawData) {
        return new ForumBasicDecoder().decode(rawData);
    }
//
//    private List<IForumDecoder> mForumDecoders = new ArrayList<>();
//
//    public ForumDecoder() {
//        this(false);
//    }
//
//    public ForumDecoder(boolean allDecoders) {
//        if (allDecoders) {
//            mForumDecoders.addAll(sDecoderPool);
//        } else {
//            mForumDecoders.add(new ForumBasicDecoder());
//        }
//    }
//
//    public String decode(String content, List<String> urls) {
//        return decode(content, urls, null);
//    }
//
//    public String decode(String content, List<String> urls, HtmlData htmlData) {
//        for (IForumDecoder decoder : mForumDecoders) {
//            content = decoder.decode(content, htmlData);
//            if (urls != null && decoder.getImageUrls() != null && !decoder.getImageUrls().isEmpty()) {
//                urls.addAll(decoder.getImageUrls());
//            }
//        }
//        return content;
//    }
//
//    public String decode(String content) {
//        return decode(content, null);
//    }
}
