package com.example.mobv2.utils.abstractions;

import androidx.annotation.NonNull;

import java.util.Map;

public interface ParsableFromMap<T>
{
    @NonNull
    T parseFromMap(@NonNull Map<String, Object> map);
}
