package com.example.mobv2.callbacks;

import com.example.mobv2.serverapi.MOBServerAPI;
import com.google.gson.internal.LinkedTreeMap;

public class PostChangeReactCallback implements MOBServerAPI.MOBAPICallback
{
    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {

    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {

    }

    @Override
    public void fail(Throwable obj)
    {

    }
}
