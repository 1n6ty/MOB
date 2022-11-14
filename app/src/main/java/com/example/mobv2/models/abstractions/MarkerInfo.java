package com.example.mobv2.models.abstractions;

import com.example.mobv2.utils.abstractions.Comparable;
import com.google.android.gms.maps.model.LatLng;

public interface MarkerInfo extends Comparable<MarkerInfo>
{
    String getId();

    void setId(String id);

    String getTitle();

    LatLng getLatLng();

    int getMarkerType();

    String getPostId();

    boolean isClicked();

    void setClicked(boolean clicked);
}
