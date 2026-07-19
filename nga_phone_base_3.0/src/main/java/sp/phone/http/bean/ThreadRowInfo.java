package sp.phone.http.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.common.base.JavaBean;

/**
 * 每一行的内容
 */
public class ThreadRowInfo implements JavaBean {

    public int tid;
    public int fid;
    public String author;//user name
    public int authorid;
    public String subject;
    public String vote;
    public String postdate;
    public int pid;
    public boolean isanonymous = false;
    public String alterinfo;// something like "edited by ..."
    public String content;
    public int lou;
    public Map<String, Attachment> attachs;
    public String level;
    public String yz; //negative integer if user is nuked
    public String js_escap_avatar;//avatar url
    public String muteTime;
    public int aurvrc;//prestige
    public String signature;
    public List<ThreadRowInfo> comments;
    public List<String> hotReplies; //热门回复

    public boolean isInBlackList;

    public String mFormattedHtmlData;

    public String from_client;
    public String from_client_model;

    public boolean mMuted;

    public String mPostCount;

    public float mReputation;

    public String mMemberGroup;

    public List<String> mImageUrlList = new ArrayList<>();

    public int score;

    public void addImageUrl(String url) {
        mImageUrlList.add(url);
    }

    public List<String> getImageUrls() {
        return mImageUrlList;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public void set_IsInBlackList(boolean isin) {
        this.isInBlackList = isin;
    }

    public boolean get_isInBlackList() {
        return isInBlackList;
    }

    public Map<String, Attachment> getAttachs() {
        return attachs;
    }

    public void setAttachs(Map<String, Attachment> attachs) {
        this.attachs = attachs;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getJs_escap_avatar() {
        return js_escap_avatar;
    }

    public void setJs_escap_avatar(String js_escap_avatar) {
        this.js_escap_avatar = js_escap_avatar;
    }

    public String getAlterinfo() {
        return alterinfo;
    }

    public void setAlterinfo(String alterinfo) {
        this.alterinfo = alterinfo;
    }

    public boolean getISANONYMOUS() {
        return isanonymous;
    }

    public void setISANONYMOUS(boolean isanonymous) {
        this.isanonymous = isanonymous;
    }

    public int getLou() {
        return lou;
    }

    public void setLou(int lou) {
        this.lou = lou;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getAuthorid() {
        return authorid;
    }

    public void setAuthorid(int authorid) {
        this.authorid = authorid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFid() {
        return fid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ThreadRowInfo> getComments() {
        return comments;
    }

    public void setComments(List<ThreadRowInfo> comments) {
        this.comments = comments;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getAurvrc() {
        return aurvrc;
    }

    public void setAurvrc(int aurvrc) {
        this.aurvrc = aurvrc;
    }

    public String getFromClient() {
        return from_client;
    }

    public void setFromClient(String from_client) {
        this.from_client = from_client;
    }

    public String getFromClientModel() {
        return from_client_model;
    }

    public void setFromClientModel(String from_client_model) {
        this.from_client_model = from_client_model;
    }


    public String getYz() {
        return yz;
    }

    public void setYz(String yz) {
        this.yz = yz;
    }

    public String getMuteTime() {
        return muteTime;
    }

    public void setMuteTime(String muteTime) {
        this.muteTime = muteTime;
    }

    public String getFormattedHtmlData() {
        return mFormattedHtmlData;
    }

    public void setFormattedHtmlData(String formattedHtmlData) {
        mFormattedHtmlData = formattedHtmlData;
    }

    public boolean isMuted() {
        return mMuted;
    }

    public void setMuted(boolean muted) {
        mMuted = muted;
    }

    public String getPostCount() {
        return mPostCount;
    }

    public void setPostCount(String postCount) {
        mPostCount = postCount;
    }

    public float getReputation() {
        return mReputation;
    }

    public void setReputation(float reputation) {
        mReputation = reputation;
    }

    public String getMemberGroup() {
        return mMemberGroup;
    }

    public void setMemberGroup(String memberGroup) {
        mMemberGroup = memberGroup;
    }
}
