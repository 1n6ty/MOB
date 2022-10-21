package com.example.mobv2.callbacks

import android.util.Log
import serverapi.MOBServerAPI.MOBAPICallback
import com.google.gson.internal.LinkedTreeMap

class MOBAPICallbackImpl : MOBAPICallback
{
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }
}