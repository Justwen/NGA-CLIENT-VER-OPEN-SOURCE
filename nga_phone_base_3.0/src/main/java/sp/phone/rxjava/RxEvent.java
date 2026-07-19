package sp.phone.rxjava;

/**
 * Created by Justwen on 2017/11/25.
 */

public class RxEvent {

    public static final int EVENT_ARTICLE_UPDATE = 0;

    public static final int EVENT_ARTICLE_GO_PAGE = 2;

    public static final int EVENT_ARTICLE_GO_FLOOR = 3;

    public static final int EVENT_ARTICLE_TAB_UPDATE = 1;

    public static final int EVENT_INSERT_EMOTICON = 5;

    public static final int EVENT_SHOW_TOPIC_LIST_BY_URL = 7;

    public static final int EVENT_CACHE_TOPIC_CONTENT = 8;

    public int what;

    public int arg;

    public Object obj;

    public RxEvent(int what) {
        this.what = what;
    }

    public RxEvent(int what, int arg) {
        this.what = what;
        this.arg = arg;
    }

    public RxEvent(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public RxEvent(int what, int arg, Object obj) {
        this.what = what;
        this.arg = arg;
        this.obj = obj;
    }
}
