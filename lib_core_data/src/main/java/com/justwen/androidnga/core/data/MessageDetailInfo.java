package com.justwen.androidnga.core.data;

import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

public class MessageDetailInfo implements JavaBean {
    int nextPage;
    int currentPage;
    String alluser;
    String title;
    List<MessageArticlePageInfo> messageEntryList;

    public int get__nextPage() {
        return nextPage;
    }

    public void set__nextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public String get_Alluser() {
        return alluser;
    }

    public void set_Alluser(String alluser) {
        this.alluser = alluser;
    }

    public String get_Title() {
        return title;
    }

    public void set_Title(String title) {
        this.title = title;
    }

    public int get__currentPage() {
        return currentPage;
    }

    public void set__currentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<MessageArticlePageInfo> getMessageEntryList() {
        return messageEntryList;
    }

    public void setMessageEntryList(List<MessageArticlePageInfo> messageEntryList) {
        this.messageEntryList = messageEntryList;
    }


}
