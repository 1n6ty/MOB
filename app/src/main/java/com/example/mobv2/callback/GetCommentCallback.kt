package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.GetCommentFailCallback
import com.example.mobv2.callback.abstraction.GetCommentOkCallback
import com.example.mobv2.ui.activity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasFailStateCallback
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

class GetCommentCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<GetCommentOkCallback>, HasFailStateCallback<GetCommentFailCallback>
{
    private var okCallback: GetCommentOkCallback? = null
    private var failCallback: GetCommentFailCallback? = null
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>

        okCallback?.parseCommentFromMapAndAddToComments(response)
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

    override fun setOkCallback(okCallback: GetCommentOkCallback)
    {
        this.okCallback = okCallback
    }

    override fun setFailCallback(failCallback: GetCommentFailCallback)
    {
        this.failCallback = failCallback
    }
}