package localdatabase.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.models.abstractions.Address;

import java.util.List;

import localdatabase.typeconverters.ListOfStringsConverter;

@Entity
public class AddressEntity implements Address
{
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "addressid")
    private String id;

    @Embedded
    private UserEntity owner;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> userIds;
    private String country;
    private String city;
    private String street;
    private String house;

    public AddressEntity(String id,
                         UserEntity owner,
                         List<String> userIds,
                         String country,
                         String city,
                         String street,
                         String house)
    {
        this.id = id;
        this.owner = owner;
        this.userIds = userIds;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public UserEntity getOwner()
    {
        return owner;
    }

    @Override
    public List<String> getUserIds()
    {
        return userIds;
    }

    @Override
    public String getPrimary()
    {
        return country + ", " + city;
    }

    @Override
    public String getSecondary()
    {
        return street + ", " + house;
    }

    @Override
    public String getCountry()
    {
        return country;
    }

    @Override
    public String getCity()
    {
        return city;
    }

    @Override
    public String getStreet()
    {
        return street;
    }

    @Override
    public String getHouse()
    {
        return house;
    }
}
