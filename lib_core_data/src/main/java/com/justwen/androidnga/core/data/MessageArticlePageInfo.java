package com.justwen.androidnga.core.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

public class MessageArticlePageInfo implements JavaBean {

    private String subject;
    private String time;
    private String content;
    private String from;
    private int lou;
    private String js_escap_avatar;//avatar url
    private String author;//user name
    private String yz; //negative integer if user is nuked
    private String mute_time;
    private String signature;
    private String formated_html_data;

    private List<Pair<String, Boolean>> contentSections = new ArrayList<>();

    public List<Pair<String, Boolean>> getContentSections() {
        return contentSections;
    }

    public void setContentSections(List<Pair<String, Boolean>> contentSections) {
        this.contentSections = contentSections;
    }

    public int getLou() {
        return lou;
    }

    public void setLou(int lou) {
        this.lou = lou;
    }

    public String getTime() {
        return time == null ? "22 13" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content == null ? "content" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject == null ? "subject" : subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJs_escap_avatar() {
        return js_escap_avatar;
    }

    public void setJs_escap_avatar(String js_escap_avatar) {
        this.js_escap_avatar = js_escap_avatar;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYz() {
        return yz;
    }

    public void setYz(String yz) {
        this.yz = yz;
    }

    public String getMute_time() {
        return mute_time;
    }

    public void setMute_time(String mute_time) {
        this.mute_time = mute_time;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFormated_html_data() {
        return formated_html_data;
    }

    public void setFormated_html_data(String formated_html_data) {
        this.formated_html_data = formated_html_data;
    }

}
