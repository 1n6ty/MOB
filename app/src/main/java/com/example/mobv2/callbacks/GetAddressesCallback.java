package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.ChangeAddressesFragment;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class GetAddressesCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final ChangeAddressesFragment.Callback callback;

    public GetAddressesCallback(MainActivity mainActivity,
                                ChangeAddressesFragment.Callback callback)
    {
        this.mainActivity = mainActivity;
        this.callback = callback;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");
        var addressesMapList = (List<LinkedTreeMap<String, Object>>) response.get("addresses");

        callback.parseAddressesFromMapListAndAddToAddresses(addressesMapList);
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
