package com.example.mobv2.callback.abstraction;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface CreatePostOkCallback
{
    void parseMarkerInfoFromMapWithLatLngAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map,
                                                                LatLng position);
}