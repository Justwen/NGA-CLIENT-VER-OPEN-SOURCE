package sp.phone.http.bean;


import gov.anzong.androidnga.common.base.JavaBean;

public class MissionDetialData implements JavaBean {//给任务具体信息载入用的
    private String detail;
    private int id;
    private String info;
    private String name;
    private String stat;
    private boolean issuccessed = false;


    public int get__id() {
        return id;
    }

    public void set__id(int id) {
        this.id = id;
    }

    public boolean get__issuccessed() {
        return issuccessed;
    }

    public void set__issuccessed(boolean issuccessed) {
        this.issuccessed = issuccessed;
    }

    public String get__name() {
        return name;
    }

    public void set__name(String name) {
        this.name = name;
    }

    public String get__info() {
        return info;
    }

    public void set__info(String info) {
        this.info = info;
    }

    public String get__detail() {
        return detail;
    }

    public void set__detail(String detail) {
        this.detail = detail;
    }

    public String get__stat() {
        return stat;
    }

    public void set__stat(String stat) {
        this.stat = stat;
    }
}
