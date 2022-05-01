package com.example.mobv2.ui.fragments.main;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MainFragmentViewModel extends ViewModel
{
    private CameraUpdate lastCoordinates =
            CameraUpdateFactory.newLatLngZoom(new LatLng(55.0415, 82.9346),
                    MainFragment.ZOOM);


    private Marker marker;

    private boolean mapReady = false;

    public Marker getMarker()
    {
        return marker;
    }

    public void setMarker(Marker marker)
    {
        this.marker = marker;
    }


    public boolean isMapReady()
    {
        return mapReady;
    }

    public void setMapReady(boolean mapReady)
    {
        this.mapReady = mapReady;
    }


    public CameraUpdate getLastCoordinates()
    {
        return lastCoordinates;
    }

    public void setLastCoordinates(CameraUpdate lastCoordinates)
    {
        this.lastCoordinates = lastCoordinates;
    }
}
