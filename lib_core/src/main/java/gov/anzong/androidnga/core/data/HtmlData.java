package gov.anzong.androidnga.core.data;

import java.util.List;

public class HtmlData implements Cloneable {

    public String uid;

    public String pid;

    public String tid;

    private boolean mInBackList;

    private boolean mDarkMode;

    private String mSubject;

    private String mRawData;

    private String mAlertInfo;

    private int mTextSize;

    private int mEmotionSize;

    private int mTableTextSize;

    private boolean mShowImage = true;

    private String mVote;

    private String mSignature;

    private String mNGAHost;

    private List<CommentData> mCommentList;

    private List<AttachmentData> mAttachmentList;

    public HtmlData(String rawData) {
        mRawData = rawData;
    }

    public boolean isInBackList() {
        return mInBackList;
    }

    public void setInBackList(boolean inBackList) {
        mInBackList = inBackList;
    }

    public boolean isDarkMode() {
        return mDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        mDarkMode = darkMode;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getAlertInfo() {
        return mAlertInfo;
    }

    public void setAlertInfo(String alertInfo) {
        mAlertInfo = alertInfo;
    }

    public String getRawData() {
        return mRawData;
    }

    public void setRawData(String rawData) {
        mRawData = rawData;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTableTextSize = (int) (textSize * 0.9);
    }

    public int getEmotionSize() {
        return mEmotionSize;
    }

    public void setEmotionSize(int emotionSize) {
        mEmotionSize = emotionSize;
    }

    public int getTableTextSize() {
        return mTableTextSize;
    }

    public boolean isShowImage() {
        return mShowImage;
    }

    public void setShowImage(boolean showImage) {
        mShowImage = showImage;
    }

    public String getVote() {
        return mVote;
    }

    public void setVote(String vote) {
        mVote = vote;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public List<CommentData> getCommentList() {
        return mCommentList;
    }

    public void setCommentList(List<CommentData> commentList) {
        mCommentList = commentList;
    }

    public List<AttachmentData> getAttachmentList() {
        return mAttachmentList;
    }

    public void setAttachmentList(List<AttachmentData> attachmentList) {
        mAttachmentList = attachmentList;
    }

    public String getNGAHost() {
        return mNGAHost;
    }

    public void setNGAHost(String NGAHost) {
        mNGAHost = NGAHost;
    }

    public static HtmlData create(String rawData, String host) {
        HtmlData htmlData = new HtmlData(rawData);
        htmlData.setNGAHost(host);
        return htmlData;
    }

}
