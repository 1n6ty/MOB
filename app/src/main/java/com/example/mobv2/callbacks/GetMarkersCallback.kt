package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.GetMarkersOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class GetMarkersCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<GetMarkersOkCallback>
{
    private var okCallback: GetMarkersOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parseMarkersFromMapAndAddToMarkerInfoList(response)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun setOkCallback(callback: GetMarkersOkCallback)
    {
        this.okCallback = callback
    }
}