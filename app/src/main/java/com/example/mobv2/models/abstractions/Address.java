package com.example.mobv2.models.abstractions;

import java.util.List;

public interface Address
{
    String getId();

    List<String> getUserIds();

    User getOwner();

    String getPrimary();

    String getSecondary();

    String getCountry();

    String getCity();

    String getStreet();

    String getHouse();
}
