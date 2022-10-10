package com.example.mobv2.callbacks;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.mobv2.R;
import com.example.mobv2.models.User;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragment;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

public class AuthCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;

    public AuthCallback(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        MainActivity.token = (String) response.get("token");

        User user = new User.UserBuilder().parseFromMap((Map<String, Object>) response.get("user"));

        SharedPreferences.Editor editor = mainActivity.getPrivatePreferences()
                                                      .edit();
        editor.putString(MainActivity.USER_ID_KEY, user.getId());
        editor.putString(MainActivity.USER_AVATAR_URL_KEY, user.getAvatarUrl());
        editor.putString(MainActivity.USER_NICKNAME_KEY, user.getNickName());
        editor.putString(MainActivity.USER_FULLNAME_KEY, user.getFullname());
        editor.putString(MainActivity.USER_EMAIL_KEY, user.getEmail());
        editor.putString(MainActivity.USER_PHONE_NUMBER_KEY, user.getPhoneNumber());
        editor.apply();

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
