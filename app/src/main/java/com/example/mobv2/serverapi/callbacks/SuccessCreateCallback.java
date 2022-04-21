package com.example.mobv2.serverapi.callbacks;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class SuccessCreateCallback extends BaseSuccessCallback<HashMap<String, Integer>,
        Function<Integer, Void>,
        Function<Integer, Void>>
{


    public SuccessCreateCallback(Function<Integer, Void> funcOk,
                                 Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, Integer>> call,
                           @NonNull Response<HashMap<String, Integer>> response)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if (response.isSuccessful())
            {
                funcOk.apply(response.body()
                                     .get("id"));
            }
            else
            {
                funcBad.apply(response.code());
            }
        }
    }
}
