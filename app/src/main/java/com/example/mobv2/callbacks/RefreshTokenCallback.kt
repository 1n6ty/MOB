package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.RefreshTokenOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class RefreshTokenCallback(private val mainActivity: MainActivity) : MOBAPICallback,
        HasOkStateCallback<RefreshTokenOkCallback>
{
    private var okCallback: RefreshTokenOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.refreshToken(response);
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun setOkCallback(okCallback: RefreshTokenOkCallback?)
    {
        this.okCallback = okCallback
    }
}