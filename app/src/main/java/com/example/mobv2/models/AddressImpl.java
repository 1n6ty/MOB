package com.example.mobv2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.models.abstractions.Address;
import com.example.mobv2.utils.abstractions.Comparable;
import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import localdatabase.typeconverters.LatLngConverter;
import localdatabase.typeconverters.StringListConverter;

@Entity
public class AddressImpl implements Address, Comparable<Address>
{
    public static final String WITHOUT_ID = "-1";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "addressid")
    private String id;

    @Embedded
    private UserImpl owner;
    @TypeConverters(StringListConverter.class)
    private List<String> userIds;
    private String country;
    private String city;
    private String street;
    private String house;

    @TypeConverters(LatLngConverter.class)
    private LatLng position;

    @ColumnInfo(name = "addresscurrent")
    private boolean current;

    @Ignore
    public AddressImpl()
    {
    }

    public AddressImpl(String id,
                       UserImpl owner,
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

    public static AddressImpl createRawAddress(String country,
                                           String city,
                                           String street,
                                           String house)
    {
        return new AddressImpl(WITHOUT_ID, null, new ArrayList<>(), country, city, street, house);
    }

    @Override
    public boolean compareById(Address address)
    {
        if (address == null) return false;

        return id.equals(address.getId());
    }

    public static class AddressBuilder implements ParsableFromMap<Address>
    {
        private String id;
        private String country;
        private String city;
        private String street;
        private String house;

        private List<String> userIds;
        private UserImpl owner;

        public AddressImpl parseFromString(String addressString)
        {
            String[] addressStrings = addressString.split(", ");

            country = addressStrings[0];
            city = addressStrings[1];
            street = addressStrings[2];
            house = addressStrings[3];

            return AddressImpl.createRawAddress(country, city, street, house);
        }

        @NonNull
        @Override
        public AddressImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            parseIdFromMap(map);
            parseFullInfoFromMap(map);
            parseUserIdsFromMap(map);
            parseOwnerFromMap(map);

            return new AddressImpl(id, owner, userIds, country, city, street, house);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = String.valueOf(((Double) map.get("id")).intValue());
        }

        private void parseFullInfoFromMap(Map<String, Object> map)
        {
            country = (String) map.get("country");
            city = (String) map.get("city");
            street = (String) map.get("street");
            house = (String) map.get("house");
        }

        private void parseUserIdsFromMap(Map<String, Object> map)
        {
            userIds = new ArrayList<>();
            var rawUserIds = (ArrayList<Double>) map.get("user_ids");
            for (Double id : rawUserIds)
            {
                userIds.add(String.valueOf(id.intValue()));
            }
        }

        private void parseOwnerFromMap(Map<String, Object> map)
        {
            var userMap = (LinkedTreeMap<String, Object>) map.get("owner");
            owner = new UserImpl.UserBuilder().parseFromMap(userMap);
        }
    }

    @NonNull
    @Override
    public String toString()
    {
        return getPrimary() + ", " + getSecondary();
    }

    public String getId()
    {
        return id;
    }

    public String getPrimary()
    {
        return country + ", " + city;
    }

    public String getSecondary()
    {
        return street + ", " + house;
    }

    public String getCountry()
    {
        return country;
    }

    public String getCity()
    {
        return city;
    }

    public String getStreet()
    {
        return street;
    }

    public String getHouse()
    {
        return house;
    }

    @Override
    public UserImpl getOwner()
    {
        return owner;
    }

    @Override
    public List<String> getUserIds()
    {
        return userIds;
    }

    public LatLng getPosition()
    {
        return position;
    }

    public void setPosition(LatLng position)
    {
        this.position = position;
    }

    @Override
    public boolean isCurrent()
    {
        return current;
    }

    @Override
    public void setCurrent(boolean current)
    {
        this.current = current;
    }
}
