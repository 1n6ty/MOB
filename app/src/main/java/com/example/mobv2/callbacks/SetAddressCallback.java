package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

public class SetAddressCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;

    public SetAddressCallback(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, String>) obj.get("response");

        MainActivity.token = response.get("token");
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
