package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.adapters.MapAdapter;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import serverapi.MOBServerAPI;

public class GetMarksCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final MapAdapter.Callback callback;

    public GetMarksCallback(MainActivity mainActivity,
                            MapAdapter.Callback callback)
    {
        this.mainActivity = mainActivity;
        this.callback = callback;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        callback.parseMarkersFromMapAndAddToMarkers(response);
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
