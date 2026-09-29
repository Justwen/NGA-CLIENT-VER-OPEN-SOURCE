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

    String getCookie();

    String getNextCookie();

    String getUserId();

    String getUserName();

    // 黑名单

    void addToBlackList(String authorName, String authorId);

    void removeFromBlackList(String authorId);

    boolean checkBlackList(String authorId);

    String getAvatarUrl(String uid);

    void clearAvatarUrl();

}
