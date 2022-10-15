package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import localdatabase.models.CommentEntity;

@Dao
public interface CommentDao
{
    @Query("SELECT * FROM commententity")
    List<CommentEntity> getAll();

    @Query("SELECT * FROM commententity WHERE commentid = :id")
    CommentEntity getById(String id);

    @Insert
    void insert(CommentEntity comment);

    @Update
    void update(CommentEntity comment);

    @Delete
    void delete(CommentEntity comment);
}
