package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.CommentOkCallback
import com.example.mobv2.ui.activity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import serverAPI.MOBServerAPI.MOBAPICallback
import com.google.gson.internal.LinkedTreeMap

class CommentCallback(private val mainActivity: MainActivity) : MOBAPICallback, HasOkStateCallback<CommentOkCallback>
{
    private var okCallback: CommentOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?
        val commentId = (response!!["id"] as Double?)!!.toInt().toString()

        okCallback?.createCommentByIdAndAddToCommentIds(commentId)
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable)
    {
        Log.v("DEBUG", obj.toString())
    }

    override fun setOkCallback(okCallback: CommentOkCallback)
    {
        this.okCallback = okCallback;
    }
}