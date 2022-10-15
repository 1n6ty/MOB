package com.example.mobv2.models;

import androidx.annotation.NonNull;

import com.example.mobv2.models.abstractions.Address;
import com.example.mobv2.models.abstractions.User;
import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddressImpl implements Address
{
    public static final String WITHOUT_ID = "-1";

    private final String id;

    private final User owner;
    private final List<String> userIds;
    private final String country;
    private final String city;
    private final String street;
    private final String house;


    private AddressImpl(String id,
                        User owner,
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

    public static Address createRawAddress(String country,
                                           String city,
                                           String street,
                                           String house)
    {
        return new AddressImpl(WITHOUT_ID, null, new ArrayList<>(), country, city, street, house);
    }

    public static class AddressBuilder implements ParsableFromMap<Address>
    {
        private String id;
        private String country;
        private String city;
        private String street;
        private String house;

        private List<String> userIds;
        private User owner;

        public Address parseFromString(String addressString)
        {
            String[] addressStrings = addressString.split(", ");

            country = addressStrings[0];
            city = addressStrings[1];
            street = addressStrings[2];
            house = addressStrings[3];

            return AddressImpl.createRawAddress(country, city, street, house);
        }

        @Override
        public Address parseFromMap(Map<String, Object> map)
        {
            if (map == null) return null;

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
            var userMap = (LinkedTreeMap<String, Object>) map.get("user");
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
    public List<String> getUserIds()
    {
        return userIds;
    }

    @Override
    public User getOwner()
    {
        return owner;
    }
}
