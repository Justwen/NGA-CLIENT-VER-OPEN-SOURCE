package sp.phone.http.bean;


import gov.anzong.androidnga.common.base.JavaBean;

public class AdminForumsData implements JavaBean {//给任务具体信息载入用的

    private String mFid;

    private String mForumName;

    public AdminForumsData(String fid, String forumName) {
        mFid = fid;
        mForumName = forumName;
    }

    public String getFid() {
        return mFid;
    }

    public String getForumName() {
        return mForumName;
    }

}
