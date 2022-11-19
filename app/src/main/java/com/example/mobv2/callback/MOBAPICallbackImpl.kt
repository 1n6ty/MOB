package com.example.mobv2.callback

import android.util.Log
import serverAPI.MOBServerAPI.MOBAPICallback
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