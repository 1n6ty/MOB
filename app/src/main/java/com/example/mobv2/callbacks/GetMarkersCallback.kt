package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.GetMarkersFailCallback
import com.example.mobv2.callbacks.abstractions.GetMarkersOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasFailStateCallback
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class GetMarkersCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<GetMarkersOkCallback>, HasFailStateCallback<GetMarkersFailCallback>
{
    private var okCallback: GetMarkersOkCallback? = null
    private var failCallback: GetMarkersFailCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parseMarkerInfosFromMapAndAddToMarkerInfoList(response)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())

        failCallback?.onDisconnect()
    }

    override fun setOkCallback(okCallback: GetMarkersOkCallback)
    {
        this.okCallback = okCallback
    }

    override fun setFailCallback(failCallback: GetMarkersFailCallback?)
    {
        this.failCallback = failCallback
    }
}