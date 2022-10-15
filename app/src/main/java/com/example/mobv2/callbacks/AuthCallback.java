package com.example.mobv2.callbacks;

import android.util.Log;
import android.widget.Toast;

import com.example.mobv2.R;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.main.MainFragment;
import com.google.gson.internal.LinkedTreeMap;

import serverapi.MOBServerAPI;

public class AuthCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final AuthFragment.Callback callback;

    public AuthCallback(MainActivity mainActivity,
                        AuthFragment.Callback callback)
    {
        this.mainActivity = mainActivity;
        this.callback = callback;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        callback.parseUserInfoFromMapAndAddToLocalDatabase(response);

        mainActivity.replaceFragment(new MainFragment());
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
        Toast.makeText(mainActivity, R.string.user_is_not_exist, Toast.LENGTH_LONG)
             .show();
    }

    @Override
    public void fail(Throwable obj)
    {
        Log.v("DEBUG", obj.toString());
        Toast.makeText(mainActivity, R.string.check_internet_connection, Toast.LENGTH_LONG)
             .show();
    }
}
