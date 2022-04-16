package com.example.mobv2.serverapi.callbacks;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class SuccessStringCallback extends BaseSuccessCallback<HashMap<String, String>, Function<String, Void>, Function<Integer, Void>>
{
    public SuccessStringCallback(Function<String, Void> funcOk, Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, String>> call, @NonNull Response<HashMap<String, String>> response)
    {
        if (response.isSuccessful())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                funcOk.apply(response.body().get("token"));
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
    public void onFailure(@NonNull Call<HashMap<String, String>> call, @NonNull Throwable t)
    {
        Log.v("ERROR", t.toString());
    }

}
