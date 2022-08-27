package com.example.mobv2.models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;

public class MarkerInfo
{
    public static final int ADDRESS_MARKER = 0, COMMON_MARKER = 1;

    private final Marker marker;
    private final int markerType;
    private boolean clicked;

    public MarkerInfo(Marker marker,
                      int markerType)
    {
        this.marker = marker;
        this.markerType = markerType;
        this.clicked = false;
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

    public boolean isClicked()
    {
        return clicked;
    }

    public void setClicked(boolean clicked)
    {
        this.clicked = clicked;
    }
}
