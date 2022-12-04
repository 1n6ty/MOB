package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.CommentOkCallback
import com.example.mobv2.ui.activity.mainActivity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI.MOBAPICallback

class CommentCallback(private val mainActivity: MainActivity, private var messageText: String) :
    MOBAPICallback, HasOkStateCallback<CommentOkCallback>
{
    private var okCallback: CommentOkCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>)
    {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?
        val commentId = (response!!["id"] as Double?)!!.toInt().toString()

        okCallback?.createCommentByIdAndTextAndAddToCommentIds(commentId, messageText)
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