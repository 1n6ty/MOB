package com.example.mobv2.callbacks.abstractions;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface GetCommentOkCallback
{
    void parseCommentFromMapAndAddToComments(LinkedTreeMap<String, Object> map);
}
