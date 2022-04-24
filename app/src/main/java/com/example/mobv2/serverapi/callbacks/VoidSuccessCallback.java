package com.example.mobv2.serverapi.callbacks;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class VoidSuccessCallback extends BaseSuccessCallback<Void,
        Function<Integer, Void>,
        Function<Integer, Void>>
{
    public VoidSuccessCallback(Function<Integer, Void> funcOk,
                               Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<Void> call,
                           @NonNull Response<Void> response)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if (response.isSuccessful())
            {
                funcOk.apply(response.code());
            }
            else
            {
                funcBad.apply(response.code());
            }
        }
    }
}
