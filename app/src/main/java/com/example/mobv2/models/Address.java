package com.example.mobv2.models;

import androidx.annotation.NonNull;

import com.example.mobv2.utils.abstractions.ParsableFromMap;

import java.util.Map;

public class Address
{
    public static final String WITHOUT_ID = "-1";

    private final String id;

    private final String country;
    private final String city;
    private final String street;
    private final int house;

    public Address(String country,
                   String city,
                   String street,
                   int house)
    {
        id = WITHOUT_ID;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    private Address(String id,
                    String country,
                    String city,
                    String street,
                    int house)
    {
        this.id = id;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public static class AddressBuilder implements ParsableFromMap<Address>
    {
        private String id;
        private String country;
        private String city;
        private String street;
        private int house;

        public Address parseFromString(String addressString)
        {
            String[] addressStrings = addressString.split(", ");

            country = addressStrings[0];
            city = addressStrings[1];
            street = addressStrings[2];
            house = Integer.parseInt(addressStrings[3]);

            return new Address(country, city, street, house);
        }

        @Override
        public Address parseFromMap(Map<String, Object> map)
        {
            if (map == null)
                return null;

            parseIdFromMap(map);
            parseFullInfoFromMap(map);

            return new Address(id, country, city, street, house);
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
            house = ((Double) map.get("house")).intValue();
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

    public int getHouse()
    {
        return house;
    }
}
