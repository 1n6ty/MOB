package localDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.model.CommentImpl;

import java.util.List;

@Dao
public interface CommentDao
{
    @Query("SELECT * FROM commentimpl")
    List<CommentImpl> getAll();

    @Query("SELECT * FROM commentimpl WHERE commentid = :id")
    CommentImpl getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CommentImpl comment);

    @Update
    void update(CommentImpl comment);

    @Delete
    void delete(CommentImpl comment);
}
