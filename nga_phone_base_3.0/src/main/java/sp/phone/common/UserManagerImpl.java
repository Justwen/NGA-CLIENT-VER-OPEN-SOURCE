package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.justwent.androidnga.bu.UserManager;

import java.util.List;

import gov.anzong.androidnga.activity.compose.filter.FilterManager;
import gov.anzong.androidnga.common.PreferenceKey;


public class UserManagerImpl implements sp.phone.common.UserManager {

    private SharedPreferences mAvatarPreferences;

    private static class SingletonHolder {

        static UserManagerImpl sInstance = new UserManagerImpl();
    }

    public static UserManagerImpl  getInstance() {
        return SingletonHolder.sInstance;
    }


    private UserManagerImpl() {
    }

    @Override
    public void initialize(Context context) {
        mAvatarPreferences = context.getSharedPreferences(PreferenceKey.PREFERENCE_AVATAR, Context.MODE_PRIVATE);
    }


    @Override
    public int getActiveUserIndex() {
        return UserManager.INSTANCE.getActiveIndex();
    }

    @Nullable
    @Override
    public User getActiveUser() {
        return UserManager.INSTANCE.getActiveUser();
    }

    @Override
    public List<User> getUserList() {
        return UserManager.INSTANCE.getUserList();
    }

    @Override
    public boolean hasValidUser() {
        return UserManager.INSTANCE.hasValidUser();
    }

    @Override
    public String getCid() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getCid() : "";
    }

    @Override
    public String getUserName() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getNickName() : "";
    }

    @Override
    public void setAvatarUrl(int userId, String url) {
        UserManager.INSTANCE.setAvatarUrl(String.valueOf(userId), url);
    }

    @Override
    public String getUserId() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getUserId() : "";
    }

    @Override
    public void setActiveUser(int index) {
        UserManager.INSTANCE.setActiveIndex(index);
    }

    @Override
    public int toggleUser(boolean isNext) {
        return UserManager.INSTANCE.toggleUser(isNext);

    }

    private int getNextActiveIndex(boolean isNext) {
        return UserManager.INSTANCE.getNextActiveIndex(isNext);
    }

    @Override
    public void addUser(User user) {
        UserManager.INSTANCE.addUser(user);
    }

    @Override
    public void addUser(String uid, String cid, String name) {
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        addUser(user);
    }

    @Override
    public void removeUser(int index) {
        UserManager.INSTANCE.removeUser(index);
    }

    @Override
    public String getCookie() {
        return UserManager.INSTANCE.getCookie(UserManager.INSTANCE.getActiveUser());
    }

    @Override
    public String getCookie(User user) {
        return UserManager.INSTANCE.getCookie(user);
    }

    @Override
    public String getNextCookie() {
        return UserManager.INSTANCE.getNextCookie();
    }

    @Override
    public void swapUser(int from, int to) {
        //if (from < to) {
        //    for (int i = from; i < to; i++) {
        //        Collections.swap(mUserList, i, i + 1);
        //    }
        //} else {
        //    for (int i = from; i > to; i--) {
        //        Collections.swap(mUserList, i, i - 1);
        //    }
        //}
        //commit();
    }

    @Override
    public void addToBlackList(String authorName, String authorId) {
        FilterManager.INSTANCE.addFilterUser(authorName,authorId);
    }


    @Override
    public void removeFromBlackList(String authorId) {
        FilterManager.INSTANCE.removeFilterUser(authorId);
    }

    @Override
    public int getUserSize() {
        return UserManager.INSTANCE.getUserList().size();
    }

    @Override
    public boolean checkBlackList(String authorId) {
        return FilterManager.INSTANCE.filterUserById(authorId);
    }

    @Override
    public String getAvatarUrl(String uid) {
        return ""; //TextUtils.isEmpty(uid) || uid.equals("0") ? "" : mAvatarPreferences.getString(uid, "");
    }

    @Override
    public void clearAvatarUrl() {
        mAvatarPreferences.edit().clear().apply();
    }
}
