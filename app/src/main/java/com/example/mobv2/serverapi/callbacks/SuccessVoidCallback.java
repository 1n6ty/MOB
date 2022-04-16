package com.example.mobv2.serverapi.callbacks;

import android.os.Build;
import android.util.Log;

import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuccessVoidCallback implements Callback<Void>
{
    private Function<Integer, Void> funcOk;
    private Function<Integer, Void> funcBad;

    public SuccessVoidCallback(Function<Integer, Void> funcOk, Function<Integer, Void> funcBad)
    {
        this.funcOk = funcOk;
        this.funcBad = funcBad;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response)
    {
        int a  = 2;

        if (response.isSuccessful())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                funcOk.apply(response.code());
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                funcBad.apply(response.code());
            }
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t)
    {
        Log.v("ERROR", t.toString());
    }
}
