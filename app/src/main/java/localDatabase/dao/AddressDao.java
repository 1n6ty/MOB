package localDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.model.AddressImpl;

import java.util.List;

@Dao
public interface AddressDao
{
    @Query("SELECT * FROM addressimpl")
    List<AddressImpl> getAll();

    @Query("SELECT * FROM addressimpl WHERE addressid = :id")
    AddressImpl getById(String id);

    @Query("SELECT * FROM addressimpl WHERE addresscurrent")
    AddressImpl getCurrentOne();

    @Query("SELECT addressid FROM addressimpl WHERE addresscurrent")
    String getCurrentId();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AddressImpl address);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(AddressImpl address);

    @Delete
    void delete(AddressImpl address);
}
