package localdatabase.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.models.AddressImpl;

import java.util.List;

@Dao
public interface AddressDao
{
    @Query("SELECT * FROM addressimpl")
    List<AddressImpl> getAll();

    @Query("SELECT * FROM addressimpl WHERE addressid = :id")
    AddressImpl getById(String id);

    @Query("SELECT * FROM addressimpl WHERE current")
    AddressImpl getCurrent();

    @Query("SELECT addressid FROM addressimpl WHERE current")
    String getCurrentId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AddressImpl address);

    @Update
    void update(AddressImpl address);

    @Delete
    void delete(AddressImpl address);
}
