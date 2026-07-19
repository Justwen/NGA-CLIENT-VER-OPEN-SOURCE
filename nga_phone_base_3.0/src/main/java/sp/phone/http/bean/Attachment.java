package sp.phone.http.bean;

import gov.anzong.androidnga.common.base.JavaBean;

public class Attachment implements JavaBean {
    private String aid;
    private String url_utf8_org_name;
    private String dscp;
    private int size;
    private String ext;
    private String name;
    private String thumb;
    private String attachurl;
    private String type;
    private int subid;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getUrl_utf8_org_name() {
        return url_utf8_org_name;
    }

    public void setUrl_utf8_org_name(String url_utf8_org_name) {
        this.url_utf8_org_name = url_utf8_org_name;
    }

    public String getDscp() {
        return dscp;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAttachurl() {
        return attachurl;
    }

    public void setAttachurl(String attachurl) {
        this.attachurl = attachurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSubid() {
        return subid;
    }

    public void setSubid(int subid) {
        this.subid = subid;
    }


}
