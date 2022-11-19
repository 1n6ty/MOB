package com.example.mobv2.callback.abstraction;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface GetPostOkCallback
{
    void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map);
}
