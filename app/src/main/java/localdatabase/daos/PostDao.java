package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import localdatabase.models.PostEntity;

@Dao
public interface PostDao
{
    @Query("SELECT * FROM postentity")
    List<PostEntity> getAll();

    @Query("SELECT * FROM postentity WHERE postid = :id")
    PostEntity getById(String id);

    @Insert
    void insert(PostEntity post);

    @Update
    void update(PostEntity post);

    @Delete
    void delete(PostEntity post);
}
