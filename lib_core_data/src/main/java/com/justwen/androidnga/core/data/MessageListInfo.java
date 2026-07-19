package com.justwen.androidnga.core.data;

import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

public class MessageListInfo implements JavaBean {
    int rowsPerPage;
    int nextPage;
    int currentPage;
    List<MessageThreadPageInfo> messageEntryList;

    public int get__rowsPerPage() {
        return rowsPerPage;
    }

    public void set__rowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public int get__nextPage() {
        return nextPage;
    }

    public void set__nextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int get__currentPage() {
        return currentPage;
    }

    public void set__currentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<MessageThreadPageInfo> getMessageEntryList() {
        return messageEntryList;
    }

    public void setMessageEntryList(List<MessageThreadPageInfo> messageEntryList) {
        this.messageEntryList = messageEntryList;
    }

    @Override
    public String toString() {
        return "MessageListInfo{" +
                "rowsPerPage=" + rowsPerPage +
                ", nextPage=" + nextPage +
                ", currentPage=" + currentPage +
                ", messageEntryList=" + messageEntryList +
                '}';
    }
}
