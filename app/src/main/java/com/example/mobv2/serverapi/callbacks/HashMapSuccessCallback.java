package com.example.mobv2.serverapi.callbacks;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class HashMapSuccessCallback extends BaseSuccessCallback<HashMap<String, Object>,
        Function<HashMap<String, Object>, Void>,
        Function<Integer, Void>>
{
    public HashMapSuccessCallback(Function<HashMap<String, Object>, Void> funcOk,
                                  Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, Object>> call,
                           @NonNull Response<HashMap<String, Object>> response)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if (response.isSuccessful())
            {
                funcOk.apply(response.body());
            }
            else
            {
                funcBad.apply(response.code());
            }
        }
    }
}
