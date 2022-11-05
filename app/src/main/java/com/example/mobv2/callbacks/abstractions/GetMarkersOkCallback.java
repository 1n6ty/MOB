package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface GetMarkersOkCallback
{
    void parseMarkerInfosFromMapAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map);
}
