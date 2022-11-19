package localDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobv2.model.MarkerInfoImpl;

import java.util.List;

@Dao
public interface MarkerInfoDao
{
    @Query("SELECT * FROM markerinfoimpl")
    List<MarkerInfoImpl> getAll();

    @Query("SELECT * FROM markerinfoimpl WHERE markerinfoid = :id")
    MarkerInfoImpl getById(String id);

    @Query("SELECT * FROM markerinfoimpl WHERE addressId = :addressId")
    List<MarkerInfoImpl> getAllByAddressId(String addressId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MarkerInfoImpl markerInfo);

    @Update
    void update(MarkerInfoImpl markerInfo);

    @Delete
    void delete(MarkerInfoImpl markerInfo);
}
