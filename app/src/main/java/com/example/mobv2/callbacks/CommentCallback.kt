package com.example.mobv2.callbacks

import android.util.Log
import com.example.mobv2.callbacks.abstractions.CommentOkCallback
import com.example.mobv2.ui.activities.MainActivity
import com.example.mobv2.utils.abstractions.responseStateCallbacks.HasOkStateCallback
import serverapi.MOBServerAPI.MOBAPICallback
import com.google.gson.internal.LinkedTreeMap

class CommentCallback(private val mainActivity: MainActivity) : MOBAPICallback, HasOkStateCallback<CommentOkCallback>
{
    private var okCallback: CommentOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?
        val commentId = (response!!["id"] as Double?)!!.toInt().toString()

        okCallback?.createCommentByIdAndAddToPosts(commentId)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun setOkCallback(callback: CommentOkCallback)
    {
        this.okCallback = callback;
    }
}