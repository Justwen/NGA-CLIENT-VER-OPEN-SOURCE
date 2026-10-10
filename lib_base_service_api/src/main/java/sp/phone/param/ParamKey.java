package sp.phone.param;

/**
 * Created by Justwen on 2017/11/26.
 */

public interface ParamKey {

    // 版面ID  整数 或 逗号分隔的整数
    String KEY_FID = "fid";

    // 有些子版面是stid
    String KEY_STID = "stid";

    //主题作者用户id  整数
    String KEY_AUTHOR_ID = "authorid";

    //搜索关键字  字符串urlencode
    String KEY_KEY = "key";

    //页  整数
    String KEY_PAGE = "page";

    //搜索的版面组  取值为user时表示全部用户版 无此参数为全部非用户版
    String KEY_FID_GROUP = "fidgroup";

    //收藏的主题  为1时显示收藏的主题
    String KEY_FAVOR = "favor";

    //推荐精华加分的主题  为1时显示推荐 精华 加分的主题
    String KEY_RECOMMEND = "recommend";

    //24 小时热帖
    String KEY_TWENTYFOUR= "twentyfour";

    //主题id  整数
    String KEY_TID = "tid";

    //回复id  整数
    String KEY_PID = "pid";

    String KEY_ACTION = "action";

    String KEY_SEARCH_POST = "searchpost";

    String KEY_CONTENT = "content";

    String KEY_AUTHOR = "author";

    String KEY_TITLE = "board_name";

    String KEY_PARAM = "requestParam";

    String BOARD_HEAD = "board_head";

}
