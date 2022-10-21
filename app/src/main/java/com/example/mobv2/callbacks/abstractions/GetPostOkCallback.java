package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

public interface GetPostOkCallback
{
    void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map);
}
