package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

public interface AuthOkCallback
{
    void parseUserInfoFromMapAndAddToLocalDatabase(LinkedTreeMap<String, Object> map);
}
