package com.example.mobv2.utils.abstractions;

import java.util.Map;

public interface ParsableFromMap<T>
{
    T parseFromMap(Map<String, Object> map);
}
