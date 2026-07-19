package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import gov.anzong.androidnga.common.base.JavaBean;

public class Board implements Parcelable, JavaBean {

    private int mFid;

    private String mName;

    private int mStd;

    private BoardKey mBoardKey;

    private String mBoardHead;

    public static class BoardKey implements Parcelable, JavaBean {

        int fid;

        int stid;

        public BoardKey(int fid, int stid) {
            this.fid = fid;
            this.stid = stid;
        }

        protected BoardKey(Parcel in) {
            fid = in.readInt();
            stid = in.readInt();
        }

        public static final Creator<BoardKey> CREATOR = new Creator<BoardKey>() {
            @Override
            public BoardKey createFromParcel(Parcel in) {
                return new BoardKey(in);
            }

            @Override
            public BoardKey[] newArray(int size) {
                return new BoardKey[size];
            }
        };

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof BoardKey
                    && (fid != 0 && fid == ((BoardKey) obj).fid || stid != 0 && stid == ((BoardKey) obj).stid);
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(fid);
            dest.writeInt(stid);
        }

        @Override
        public String toString() {
            if (stid != 0) {
                return "stid=" + stid;
            } else {
                return "fid=" + fid;
            }
        }
    }

    protected Board(Parcel in) {
        mFid = in.readInt();
        mName = in.readString();
        mStd = in.readInt();
        mBoardKey = in.readParcelable(BoardKey.class.getClassLoader());
        mBoardHead = in.readString();
    }

    public Board(int fid, String name) {
        this(fid, 0, name);
    }

    public Board(int fid, int stid, String name) {
        mFid = fid;
        mName = name;
        mStd = stid;
        mBoardKey = new BoardKey(fid, stid);
    }

    public Board(BoardKey boardKey, String name) {
        mFid = boardKey.fid;
        mName = name;
        mStd = boardKey.stid;
        mBoardKey = boardKey;
    }

    public Board() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getFid() {
        return mFid;
    }

    public void setFid(int fid) {
        mFid = fid;
    }

    public int getStid() {
        return mStd;
    }

    public void setStid(int std) {
        mStd = std;
    }

    public BoardKey getBoardKey() {
        return mBoardKey;
    }

    public String getBoardHead() {
        return mBoardHead;
    }

    public void setBoardHead(String boardHead) {
        mBoardHead = boardHead;
    }

    @Deprecated
    public void fixBoardKey() {
        if (mBoardKey == null) {
            mBoardKey = new BoardKey(mFid, mStd);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Board && mBoardKey.equals(((Board) obj).getBoardKey());
    }

    public String toUrlString() {
        return mBoardKey.toString();
    }

    @Override
    public int hashCode() {
        return mFid != 0 ? mFid : getStid();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mFid);
        dest.writeString(mName);
        dest.writeInt(mStd);
        dest.writeParcelable(mBoardKey, flags);
        dest.writeString(mBoardHead);
    }

    @Override
    public String toString() {
        return "Board{" +
                "mFid=" + mFid +
                ", mName='" + mName + '\'' +
                ", mStd=" + mStd +
                ", mBoardKey=" + mBoardKey +
                ", mBoardHead='" + mBoardHead + '\'' +
                '}';
    }
}
