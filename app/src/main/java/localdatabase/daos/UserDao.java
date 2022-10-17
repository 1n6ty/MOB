package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.models.UserImpl;

/*
 * UserDatabase have to store only one user
 */
@Dao
public interface UserDao
{
    @Query("SELECT * FROM userimpl")
    UserImpl getOne();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserImpl user);

    @Update
    void update(UserImpl user);

    @Delete
    void delete(UserImpl user);

    @Query("SELECT userid FROM userimpl")
    String getId();
}
