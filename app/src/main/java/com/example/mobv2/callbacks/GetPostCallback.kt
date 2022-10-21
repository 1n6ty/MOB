package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.GetPostFailCallback
import com.example.mobv2.callbacks.abstractions.GetPostOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasFailStateCallback
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class GetPostCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<GetPostOkCallback>, HasFailStateCallback<GetPostFailCallback>
{
    private var okCallback: GetPostOkCallback? = null
    private var failCallback: GetPostFailCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.parsePostFromMapAndAddToPosts(response)
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

    override fun setOkCallback(okCallback: GetPostOkCallback)
    {
        this.okCallback = okCallback
    }

    override fun setFailCallback(failCallback: GetPostFailCallback)
    {
        this.failCallback = failCallback
    }
}