package com.example.mobv2.utils.abstractions;

public interface Operation<T, R>
{
    R execute(T arg);
}
