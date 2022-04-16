package com.example.mobv2.serverapi.callbacks;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class SuccessHashMapCallback extends BaseSuccessCallback<HashMap<String, Object>, Function<HashMap<String, Object>, Void>, Function<Integer, Void>>
{
    public SuccessHashMapCallback(Function<HashMap<String, Object>, Void> funcOk, Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, Object>> call, @NonNull Response<HashMap<String, Object>> response)
    {
        if (response.isSuccessful())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                funcOk.apply(response.body());
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
    public void onFailure(@NonNull Call<HashMap<String, Object>> call, @NonNull Throwable t)
    {
        Log.v("ERROR", t.toString());
    }

}
