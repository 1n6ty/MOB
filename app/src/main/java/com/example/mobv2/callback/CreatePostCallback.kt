package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.CreatePostOkCallback
import com.example.mobv2.ui.activity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.android.gms.maps.model.LatLng
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

class CreatePostCallback(private val mainActivity: MainActivity,
                         private val latLng: LatLng) : MOBAPICallback,
    HasOkStateCallback<CreatePostOkCallback>
{
    private var okCallback: CreatePostOkCallback? = null
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>

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