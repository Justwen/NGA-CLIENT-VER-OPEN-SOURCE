package sp.phone.common;

import android.content.Context;

import java.util.List;

public interface UserManager {

    int getUserSize();

    User getActiveUser();

    void initialize(Context context);

    int getActiveUserIndex();

    List<User> getUserList();

    boolean hasValidUser();

    void setActiveUser(int index);

    int toggleUser(boolean isNext);

    void addUser(User user);

    void addUser(String uid, String cid, String name);

    void removeUser(int index);

    void swapUser(int from, int to);

    // User 类辅助接口

    String getCookie();

    String getCookie(User user);

    String getNextCookie();

    String getUserId();

    String getCid();

    String getUserName();

    void setAvatarUrl(int userId, String url);

    // 黑名单

    void addToBlackList(String authorName, String authorId);

    void removeFromBlackList(String authorId);

    boolean checkBlackList(String authorId);

    String getAvatarUrl(String uid);

    void clearAvatarUrl();

}
