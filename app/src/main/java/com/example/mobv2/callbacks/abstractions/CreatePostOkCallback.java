package com.example.mobv2.callbacks.abstractions;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

public interface CreatePostOkCallback
{
    void parsePostIdFromMapAndAddUsingPositionToMarkerInfoList(LinkedTreeMap<String, Object> map,
                                                               LatLng position);
}
