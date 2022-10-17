package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.models.CommentImpl;

import java.util.List;

@Dao
public interface CommentDao
{
    @Query("SELECT * FROM commentimpl")
    List<CommentImpl> getAll();

    @Query("SELECT * FROM commentimpl WHERE commentid = :id")
    CommentImpl getById(String id);

    @Insert
    void insert(CommentImpl comment);

    @Update
    void update(CommentImpl comment);

    @Delete
    void delete(CommentImpl comment);
}
