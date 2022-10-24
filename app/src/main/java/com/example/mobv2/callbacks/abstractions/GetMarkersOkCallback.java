package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface GetMarkersOkCallback
{
    void parseMarkersFromMapAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map);
}
