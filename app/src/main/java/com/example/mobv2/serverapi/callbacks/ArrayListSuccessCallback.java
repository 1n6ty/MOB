package com.example.mobv2.serverapi.callbacks;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;

public class ArrayListSuccessCallback extends BaseSuccessCallback<HashMap<String, ArrayList<HashMap<String, String>>>,
        Function<ArrayList<HashMap<String, String>>, Void>,
        Function<Integer, Void>>
{
    public ArrayListSuccessCallback(Function<ArrayList<HashMap<String, String>>, Void> funcOk,
                                    Function<Integer, Void> funcBad)
    {
        super(funcOk, funcBad);
    }

    @Override
    public void onResponse(@NonNull Call<HashMap<String, ArrayList<HashMap<String, String>>>> call,
                           @NonNull Response<HashMap<String, ArrayList<HashMap<String, String>>>> response)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if (response.isSuccessful())
            {
                funcOk.apply(response.body()
                                     .get("response"));
            }
            else
            {
                funcBad.apply(response.code());
            }
        }
    }
}
