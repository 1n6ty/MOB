package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.GetPostFailCallback
import com.example.mobv2.callback.abstraction.GetPostOkCallback
import com.example.mobv2.ui.activity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasFailStateCallback
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

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