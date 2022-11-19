package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.GetMarkersFailCallback
import com.example.mobv2.callback.abstraction.GetMarkersOkCallback
import com.example.mobv2.ui.activity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasFailStateCallback
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

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