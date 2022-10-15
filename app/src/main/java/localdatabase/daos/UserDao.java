package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import localdatabase.models.UserEntity;

@Dao
public interface UserDao
{
    @Query("SELECT * FROM userentity")
    List<UserEntity> getAll();

    @Query("SELECT * FROM userentity WHERE userid = :id")
    UserEntity getById(String id);

    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);
}
