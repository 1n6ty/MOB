package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.ui.activity.mainActivity.MainActivity
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

class SetAddressCallback(private val mainActivity: MainActivity) : MOBAPICallback
{
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, String>?

        MainActivity.token = response!!["token"]
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