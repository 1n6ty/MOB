package com.example.mobv2.serverapi.callbacks;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class SuccessArrayListCallback extends
        BaseSuccessCallback<HashMap<String, ArrayList<HashMap<String, String>>>,
                Function<ArrayList<HashMap<String, String>>, Void>,
                Function<Integer, Void>>
{
    public SuccessArrayListCallback(Function<ArrayList<HashMap<String, String>>, Void> funcOk, Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, ArrayList<HashMap<String, String>>>> call, @NonNull Response<HashMap<String, ArrayList<HashMap<String, String>>>> response)
    {
        if (response.isSuccessful())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                funcOk.apply(response.body().get("response"));
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
    public void onFailure(@NonNull Call<HashMap<String, ArrayList<HashMap<String, String>>>> call, @NonNull Throwable t)
    {
        Log.v("ERROR", t.toString());
    }
}
