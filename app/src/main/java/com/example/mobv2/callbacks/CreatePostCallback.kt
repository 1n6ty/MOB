package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.CreatePostOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.android.gms.maps.model.LatLng
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class CreatePostCallback(private val mainActivity: MainActivity,
                         private val latLng: LatLng) : MOBAPICallback,
    HasOkStateCallback<CreatePostOkCallback>
{
    private var okCallback: CreatePostOkCallback? = null
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parseMarkerInfoFromMapWithLatLngAndAddToMarkerInfoList(response, latLng)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun setOkCallback(okCallback: CreatePostOkCallback)
    {
        this.okCallback = okCallback
    }
}