package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.GetCommentFailCallback
import com.example.mobv2.callbacks.abstractions.GetCommentOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasFailStateCallback
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverapi.MOBServerAPI.MOBAPICallback

class GetCommentCallback(private val mainActivity: MainActivity) : MOBAPICallback,
    HasOkStateCallback<GetCommentOkCallback>, HasFailStateCallback<GetCommentFailCallback>
{
    private var okCallback: GetCommentOkCallback? = null
    private var failCallback: GetCommentFailCallback? = null
    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

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