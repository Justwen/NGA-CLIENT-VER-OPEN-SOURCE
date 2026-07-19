package com.justwen.androidnga.core.data;

import gov.anzong.androidnga.common.base.JavaBean;

public class MessageThreadPageInfo implements JavaBean {

    private int mid;
    private String subject;
    private String time;
    private String lasttime;
    private int posts;
    private String from_username;
    private String last_from_username;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastTime() {
        return lasttime;
    }

    public void setLastTime(String lasttime) {
        this.lasttime = lasttime;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom_username() {
        return from_username;
    }

    public void setFrom_username(String from_username) {
        this.from_username = from_username;
    }

    public String getLast_from_username() {
        return last_from_username;
    }

    public void setLast_from_username(String last_from_username) {
        this.last_from_username = last_from_username;
    }

}
