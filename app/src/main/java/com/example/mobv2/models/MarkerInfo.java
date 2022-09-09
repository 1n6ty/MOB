package com.example.mobv2.models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class MarkerInfo
{
    public static final int ADDRESS_MARKER = 0, SUB_ADDRESS_MARKER = 1;
    public static final int MARKER_NOT_CLICKED = 0, MARKER_CLICKED = 1, MARKER_ADDED = 2, MARKER_REMOVED = 3;

    private final Marker marker;
    private final int markerType;
    private final Map<String, Object> metadata;
    private int markerCondition;

    public MarkerInfo(Marker marker,
                      int markerType)
    {
        this.marker = marker;
        this.markerType = markerType;
        this.metadata = new HashMap<>();
        this.markerCondition = MARKER_NOT_CLICKED;
    }

    @NonNull
    public Marker getMarker()
    {
        return marker;
    }

    public int getMarkerType()
    {
        return markerType;
    }

    public Map<String, Object> getMetadata()
    {
        return metadata;
    }

    public int getMarkerCondition()
    {
        return markerCondition;
    }

    public void setMarkerCondition(int markerCondition)
    {
        this.markerCondition = markerCondition;
    }
}
