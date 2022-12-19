package com.example.mobv2.util;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;

import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.google.android.gms.maps.model.LatLng;

public class LocationAddress
{
    @NonNull
    public static float[] getDistance(double startLatitude,
                                      double startLongitude,
                                      double endLatitude,
                                      double endLongitude)
    {
        float[] distance = new float[2];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude,
                distance);

        return distance;
    }

    public static AddressImpl getOtherAddressByLatLng(Context context,
                                                      @NonNull LatLng latLng)
    {
        try
        {
            Geocoder geocoder = new Geocoder(context, MainActivity.LOCALE);
            android.location.Address mapAddress = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1).get(0);

            AddressImpl rawAddress = AddressImpl.createRawAddress(mapAddress.getCountryName(),
                    mapAddress.getLocality(), mapAddress.getThoroughfare(),
                    mapAddress.getFeatureName());
            rawAddress.setLatLng(latLng);
            return rawAddress;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
