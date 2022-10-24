package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface RefreshTokenOkCallback
{
    void refreshToken(LinkedTreeMap<String, Object> map);
}
