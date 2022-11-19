package com.example.mobv2.model.abstraction;

import com.example.mobv2.util.abstraction.Comparable;
import com.google.android.gms.maps.model.LatLng;

public interface MarkerInfo extends Comparable<MarkerInfo>
{
    String getId();

    String getTitle();

    LatLng getLatLng();

    int getMarkerType();

    String getAddressId();

    boolean isClicked();

    void setClicked(boolean clicked);
}
