package com.example.mobv2.callbacks

import android.util.Log
import android.widget.Toast
import com.example.mobv2.R
import com.example.mobv2.callbacks.abstractions.AuthOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.ui.fragments.main.MainFragment
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class AuthCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<AuthOkCallback>
{
    private var okCallback: AuthOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parseUserInfoFromMapAndAddToLocalDatabase(response)
        mainActivity.replaceFragment(MainFragment())
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        Toast.makeText(mainActivity, R.string.user_is_not_exist, Toast.LENGTH_LONG)
            .show()
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
        Toast.makeText(mainActivity, R.string.check_internet_connection, Toast.LENGTH_LONG)
            .show()
    }

    override fun setOkCallback(callback: AuthOkCallback)
    {
        okCallback = callback
    }
}