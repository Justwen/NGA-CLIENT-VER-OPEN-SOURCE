package sp.phone.common;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import gov.anzong.androidnga.common.base.JavaBean;

/**
 * @author Justwen
 * @date 2017/12/26
 */
@Entity(tableName = "users")
public class User implements JavaBean {

    @ColumnInfo(name = "cid")
    public String mCid;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uid")
    public String mUserId;

    @ColumnInfo(name = "nick_name")
    public String mNickName;

    @ColumnInfo(name = "avatar_url")
    public String mAvatarUrl;

    public User() {
    }

    @Ignore
    public User(@NonNull String userId, String nickName, String cid) {
        mUserId = userId;
        mNickName = nickName;
        mCid = cid;
    }

    @Ignore
    public User(@NonNull String userId, String nickName) {
        mUserId = userId;
        mNickName = nickName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getCid() {
        return mCid;
    }

    public void setCid(String cid) {
        mCid = cid;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && mUserId.equals(((User) obj).getUserId());
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "mCid='" + mCid + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mNickName='" + mNickName + '\'' +
                ", mAvatarUrl='" + mAvatarUrl + '\'' +
                '}';
    }

    public String toShortString() {
        return mUserId + '/' + mNickName;
    }
}
