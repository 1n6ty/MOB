package com.example.mobv2.serverapi.callbacks;

import androidx.annotation.NonNull;

import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseSuccessCallback<T1, T2, T3> implements Callback<T1>
{
    protected T2 funcOk;
    protected T3 funcBad;

    protected BaseSuccessCallback(T2 funcOk, T3 funcBad)
    {
        this.funcOk = funcOk;
        this.funcBad = funcBad;
    }

    @Override
    public abstract void onResponse(@NonNull Call<T1> call, @NonNull Response<T1> response);

    @Override
    public abstract void onFailure(@NonNull Call<T1> call, @NonNull Throwable t);
}
