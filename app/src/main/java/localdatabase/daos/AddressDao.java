package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import localdatabase.models.AddressEntity;

@Dao
public interface AddressDao
{
    @Query("SELECT * FROM addressentity")
    List<AddressEntity> getAll();

    @Query("SELECT * FROM addressentity WHERE addressid = :id")
    AddressEntity getById(String id);

    @Insert
    void insert(AddressEntity address);

    @Update
    void update(AddressEntity address);

    @Delete
    void delete(AddressEntity address);
}
