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
    public String getUserName() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getNickName() : "";
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
    public String getCookie() {
        return UserManager.INSTANCE.getCookie(UserManager.INSTANCE.getActiveUser());
    }

    @Override
    public String getNextCookie() {
        return UserManager.INSTANCE.getNextCookie();
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
