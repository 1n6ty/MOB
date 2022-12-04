package com.example.mobv2.callback

import android.util.Log
import android.widget.Toast
import com.example.mobv2.R
import com.example.mobv2.callback.abstraction.AuthOkCallback
import com.example.mobv2.ui.activity.mainActivity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

class AuthCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<AuthOkCallback>
{
    private var okCallback: AuthOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parseUserInfoFromMapAndAddToLocalDatabase(response)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        Toast.makeText(mainActivity, R.string.user_does_not_exist, Toast.LENGTH_LONG)
            .show()
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
        Toast.makeText(mainActivity, R.string.check_internet_connection, Toast.LENGTH_LONG)
            .show()
    }

    override fun setOkCallback(okCallback: AuthOkCallback)
    {
        this.okCallback = okCallback
    }
}