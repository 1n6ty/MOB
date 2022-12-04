package com.example.mobv2.callback

import android.util.Log
import com.example.mobv2.callback.abstraction.EditUserFailCallback
import com.example.mobv2.callback.abstraction.EditUserOkCallback
import com.example.mobv2.ui.activity.mainActivity.MainActivity
import com.example.mobv2.util.abstraction.responseStateCallback.HasFailStateCallback
import com.example.mobv2.util.abstraction.responseStateCallback.HasOkStateCallback
import com.google.gson.internal.LinkedTreeMap
import serverAPI.MOBServerAPI

class EditUserCallback(private val mainActivity: MainActivity) : MOBServerAPI.MOBAPICallback,
    HasOkStateCallback<EditUserOkCallback>, HasFailStateCallback<EditUserFailCallback> {
    private var okCallback: EditUserOkCallback? = null
    private var failCallback: EditUserFailCallback? = null

    override fun funcOk(obj: LinkedTreeMap<String, Any>) {
        Log.v("DEBUG", obj.toString())
        val response = obj["response"] as LinkedTreeMap<String, Any>?

        okCallback?.editUserInLocalDatabase()
    }

    override fun funcBad(obj: LinkedTreeMap<String, Any>) {
        Log.v("DEBUG", obj.toString())
    }

    override fun fail(obj: Throwable) {
        Log.v("DEBUG", obj.toString())

        failCallback?.onDisconnect()
    }

    override fun setOkCallback(okCallback: EditUserOkCallback) {
        this.okCallback = okCallback
    }

    override fun setFailCallback(failCallback: EditUserFailCallback?) {
        this.failCallback = failCallback
    }
}