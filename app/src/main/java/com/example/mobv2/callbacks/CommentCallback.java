package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragment;
import com.google.gson.internal.LinkedTreeMap;

import serverapi.MOBServerAPI;

public class CommentCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final CommentsFragment.Callback callback;

    public CommentCallback(MainActivity mainActivity,
                           CommentsFragment.Callback callback)
    {
        this.mainActivity = mainActivity;
        this.callback = callback;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        String commentId = String.valueOf((((Double) response.get("comment_id"))).intValue());

        callback.createCommentByIdAndAddToPosts(commentId);
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
