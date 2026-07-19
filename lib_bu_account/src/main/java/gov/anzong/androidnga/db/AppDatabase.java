package gov.anzong.androidnga.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import gov.anzong.androidnga.db.user.UserDao;
import sp.phone.common.User;

/**
 * @author yangyihang
 */
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String MAIN_DB_NAME = "app_database.db";

    private static AppDatabase sInstance;

    public static void init(Context context) {
        sInstance = Room.databaseBuilder(context, AppDatabase.class, MAIN_DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public static AppDatabase getInstance() {
        return sInstance;
    }

    public abstract UserDao userDao();

}
