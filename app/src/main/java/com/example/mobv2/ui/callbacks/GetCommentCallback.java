package com.example.mobv2.ui.callbacks;

import android.util.Log;

import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.models.Comment;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.google.gson.internal.LinkedTreeMap;

public class GetCommentCallback implements MOBServerAPI.MOBAPICallback
{
    private final CommentsAdapter commentsAdapter;

    public GetCommentCallback(CommentsAdapter commentsAdapter)
    {
        this.commentsAdapter = commentsAdapter;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        Comment comment = Comment.parseFromMap(response);
        commentsAdapter.addComment(comment);
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
    }

    @Override
    public void fail(Throwable obj)
    {
        Log.v("DEBUG", obj.toString());
    }
}
