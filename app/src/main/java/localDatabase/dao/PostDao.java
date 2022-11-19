package localDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.model.PostImpl;

import java.util.List;

@Dao
public interface PostDao
{
    @Query("SELECT * FROM postimpl")
    List<PostImpl> getAll();

    @Query("SELECT * FROM postimpl WHERE postid = :id")
    PostImpl getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PostImpl post);

    @Update
    void update(PostImpl post);

    @Delete
    void delete(PostImpl post);
}
