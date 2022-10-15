package com.example.mobv2.callbacks;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import serverapi.MOBServerAPI;

public class MOBAPICallbackImpl implements MOBServerAPI.MOBAPICallback
{
    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
    }

    @Override
    public void fail(Throwable obj)
    {
        Log.v("DEBUG", obj.toString());
    }
}
