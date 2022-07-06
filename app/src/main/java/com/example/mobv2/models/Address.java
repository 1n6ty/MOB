package com.example.mobv2.models;

import androidx.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;

public class Address
{
    private final int id;

    private final String country;
    private final String city;
    private final String street;
    private final int house;
    private final double x;
    private final double y;

    public Address(int id,
                   String country,
                   String city,
                   String street,
                   int house,
                   double x,
                   double y)
    {
        this.id = id;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
        this.x = x;
        this.y = y;
    }

    public static Address parseFromMap(LinkedTreeMap<String, Object> map)
    {
        // id
        int id = ((Double) map.get("id")).intValue();

        // address
        String country = (String) map.get("country");
        String city = (String) map.get("city");
        String street = (String) map.get("street");
        int house = ((Double) map.get("house")).intValue();

        // coordinates
        double x = (double) map.get("x");
        double y = (double) map.get("y");

        return new Address(id, country, city, street, house, x, y);
    }

    @NonNull
    @Override
    public String toString()
    {
        return getPrimary() + ", " + getSecondary();
    }

    public int getId()
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

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
}
