package com.example.mobv2.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddMarker
{
    private LatLng position;
    private String title;
    private BitmapDescriptor descriptor;

    public AddMarker(double latitude,
                     double longitude,
                     String title,
                     BitmapDescriptor descriptor)
    {
        this.position = new LatLng(latitude, longitude);
        this.title = title;
        this.descriptor = descriptor;
    }

    public MarkerOptions create()
    {
        return new MarkerOptions()
                .position(position)
                .title(title)
                .icon(descriptor);
    }
}
