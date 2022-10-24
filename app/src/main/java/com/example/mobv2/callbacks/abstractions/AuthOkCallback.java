package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface AuthOkCallback
{
    void parseUserInfoFromMapAndAddToLocalDatabase(LinkedTreeMap<String, Object> map);
}
