package com.example.mobv2.util.abstraction;

import androidx.annotation.NonNull;

import java.util.Map;

public interface ParsableFromMap<T>
{
    @NonNull
    T parseFromMap(@NonNull Map<String, Object> map);
}
