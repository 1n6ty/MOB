package localDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.model.UserImpl;

import java.util.List;

@Dao
public interface UserDao
{
    @Query("SELECT * FROM userimpl")
    List<UserImpl> getAll();

    @Query("SELECT * FROM userimpl WHERE userid = :id")
    UserImpl getById(String id);

    @Query("SELECT * FROM userimpl WHERE userlastlogin")
    UserImpl getLastLoginOne();

    @Query("SELECT * FROM userimpl WHERE usercurrent")
    UserImpl getCurrentOne();

    @Query("SELECT userid FROM userimpl WHERE usercurrent")
    String getCurrentId();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserImpl user);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(UserImpl user);

    @Delete
    void delete(UserImpl user);
}
