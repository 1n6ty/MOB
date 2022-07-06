package com.example.mobv2.ui.callbacks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

public class SetAddressCallback implements MOBServerAPI.MOBAPICallback
{
    private final Context context;

    public SetAddressCallback(Context context)
    {
        this.context = context;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
        LinkedTreeMap<String, String> response =
                (LinkedTreeMap<String, String>) obj.get("response");
        Toast.makeText(context, "Successful", Toast.LENGTH_LONG)
             .show();

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
