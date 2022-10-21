package com.example.mobv2.models.abstractions;

import com.example.mobv2.utils.abstractions.Comparable;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public interface MarkerInfo extends Comparable<MarkerInfo>
{
    String getId();

    void setId(String id);

    String getTitle();

    LatLng getPosition();

    int getMarkerType();

    Map<String, Object> getMetadata();

    boolean isClicked();

    void setClicked(boolean clicked);
}
