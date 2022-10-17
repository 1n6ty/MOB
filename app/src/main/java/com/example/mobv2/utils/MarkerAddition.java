package com.example.mobv2.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerAddition
{
    private final String title;
    private final LatLng position;
    private final BitmapDescriptor descriptor;

    public MarkerAddition(double latitude,
                          double longitude)
    {
        this("", latitude, longitude);
    }

    public MarkerAddition(String title,
                          LatLng latLng)
    {
        this(title, latLng.latitude, latLng.longitude, null);
    }

    public MarkerAddition(String title,
                          double latitude,
                          double longitude)
    {
        this(title, latitude, longitude, null);
    }

    public MarkerAddition(String title,
                          double latitude,
                          double longitude,
                          BitmapDescriptor descriptor)
    {
        this.title = title;
        this.position = new LatLng(latitude, longitude);
        this.descriptor = descriptor;
    }

    public MarkerOptions create()
    {
        return new MarkerOptions()
                .title(title)
                .position(position)
                .icon(descriptor);
    }
}