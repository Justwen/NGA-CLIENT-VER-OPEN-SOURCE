package sp.phone.http.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

public class ArticlePage implements JavaBean {

    private List<Article> listArticle; //
    private HashMap<String, String> now;

    private ArrayList<HashMap<String, String>> list;//

    private HashMap<String, String> page;// pre next first last current num

    public HashMap<String, String> getNow() {
        return now;
    }

    public void setNow(HashMap<String, String> now) {
        this.now = now;
    }

    public List<Article> getListArticle() {
        return listArticle;
    }

    public void setListArticle(List<Article> listArticle) {
        this.listArticle = listArticle;
    }

    public ArrayList<HashMap<String, String>> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap<String, String>> list) {
        this.list = list;
    }

    public HashMap<String, String> getPage() {
        return page;
    }

    public void setPage(HashMap<String, String> page) {
        this.page = page;
    }

}
