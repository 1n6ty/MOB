package com.example.mobv2.callback.abstraction;

import androidx.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;

@FunctionalInterface
public interface GetCommentOkCallback
{
    void parseCommentFromMapAndAddToComments(@NonNull LinkedTreeMap<String, Object> map);
}
