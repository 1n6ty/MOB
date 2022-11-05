package com.example.mobv2.models.abstractions;

import com.example.mobv2.utils.abstractions.Comparable;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface Address extends Comparable<Address>
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

    boolean isCurrent();

    void setCurrent(boolean current);

    LatLng getLatLng();

    void setLatLng(LatLng latLng);
}
