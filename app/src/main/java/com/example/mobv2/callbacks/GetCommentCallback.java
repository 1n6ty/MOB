package com.example.mobv2.callbacks;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.models.Comment;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

public class GetCommentCallback implements MOBServerAPI.MOBAPICallback
{
    protected final MainActivity mainActivity;
    protected final RecyclerView commentsRecycler;

    public GetCommentCallback(MainActivity mainActivity, RecyclerView commentsRecycler)
    {
        this.mainActivity = mainActivity;
        this.commentsRecycler = commentsRecycler;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        Comment comment = new Comment.CommentBuilder().parseFromMap(response);
        var commentsAdapter = (CommentsAdapter) commentsRecycler.getAdapter();
        commentsAdapter.addComment(comment);
        commentsRecycler.scrollToPosition(0);
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
