package com.example.mobv2.serverapi.callbacks;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class BaseSuccessCallback<T1, T2, T3> implements Callback<T1>
{
    protected T2 funcOk;
    protected T3 funcBad;

    protected BaseSuccessCallback(T2 funcOk,
                                  T3 funcBad)
    {
        this.funcOk = funcOk;
        this.funcBad = funcBad;
    }

    @Override
    public void onFailure(Call<T1> call,
                          Throwable t)
    {
        Log.v("ERROR", t.toString());
    }
}
