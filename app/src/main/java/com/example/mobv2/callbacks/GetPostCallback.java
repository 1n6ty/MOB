package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.adapters.MapAdapter;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import serverapi.MOBServerAPI;

public class GetPostCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final MapAdapter.PostOkCallback callback;
    private final MapAdapter.PostFailCallback badCallback;

    public GetPostCallback(MainActivity mainActivity,
                           MapAdapter.PostOkCallback callback,
                           MapAdapter.PostFailCallback badCallback)
    {
        this.mainActivity = mainActivity;
        this.callback = callback;
        this.badCallback = badCallback;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        callback.parsePostFromMapAndAddToPosts(response);
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

        badCallback.onDisconnect();
    }
}
