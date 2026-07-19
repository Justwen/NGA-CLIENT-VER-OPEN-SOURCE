package gov.anzong.androidnga.db.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import sp.phone.common.User;

/**
 * @author yangyihang
 */
@Dao
public interface UserDao {

    @Query("SELECT * from users")
    List<User> loadUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateUsers(User... users);

    @Delete
    void removeUsers(User... users);

}
