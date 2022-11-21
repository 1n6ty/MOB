package com.example.mobv2.callback.abstraction;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface CreatePostOkCallback
{
    void parseMarkerInfoFromMapWithLatLngAndAddToMarkerInfoList(@NonNull LinkedTreeMap<String, Object> map,
                                                                @NonNull LatLng position);
}
