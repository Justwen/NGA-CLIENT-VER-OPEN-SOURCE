package sp.phone.http.bean;

import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

public class ProfileData implements JavaBean {

    private List<AdminForumsData> mAdminForums;

    private List<ReputationData> mReputationEntryList;

    private String mAvatarUrl;

    private String mEmailAddress;

    private String mFrame;

    private String mMemberGroup;

    private String mMoney;

    private boolean mMuted;

    private String mMutedTime;

    private boolean mNuked;

    private String mPhoneNumber;

    private String mPostCount;

    private String mRegisterDate;

    private String mSign;

    private String mUid;

    private String mUserName;

    public String ipLoc;

    public String getFrame() {
        return mFrame;
    }

    public void setFrame(String frame) {
        mFrame = frame;
    }

    public String getSign() {
        return mSign;
    }

    public void setSign(String sign) {
        mSign = sign;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPostCount() {
        return mPostCount;
    }

    public void setPostCount(String postCount) {
        mPostCount = postCount;
    }

    public String getMoney() {
        return mMoney;
    }

    public void setMoney(String money) {
        mMoney = money;
    }

    public String getRegisterDate() {
        return mRegisterDate;
    }

    public void setRegisterDate(String registerDate) {
        mRegisterDate = registerDate;
    }

    public String getMutedTime() {
        return mMutedTime;
    }

    public void setMutedTime(String mutedTime) {
        mMutedTime = mutedTime;
    }

    public boolean isMuted() {
        return mMuted;
    }

    public void setMuted(boolean muted) {
        mMuted = muted;
    }

    public boolean isNuked() {
        return mNuked;
    }

    public void setNuked(boolean nuked) {
        mNuked = nuked;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getMemberGroup() {
        return mMemberGroup;
    }

    public void setMemberGroup(String memberGroup) {
        mMemberGroup = memberGroup;
    }

    public List<AdminForumsData> getAdminForums() {
        return mAdminForums;
    }

    public void setAdminForums(List<AdminForumsData> adminForums) {
        mAdminForums = adminForums;
    }

    public List<ReputationData> getReputationEntryList() {
        return mReputationEntryList;
    }

    public void setReputationEntryList(List<ReputationData> reputationEntryList) {
        mReputationEntryList = reputationEntryList;
    }
}