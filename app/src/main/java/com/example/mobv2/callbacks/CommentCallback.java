package com.example.mobv2.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

public class CommentCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final RecyclerView commentsRecycler;
    private final int postId;

    public CommentCallback(MainActivity mainActivity,
                           RecyclerView commentsRecycler,
                           int postId)
    {
        this.mainActivity = mainActivity;
        this.commentsRecycler = commentsRecycler;
        this.postId = postId;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        var commentsAdapter = (CommentsAdapter) commentsRecycler.getAdapter();
        MainActivity.MOB_SERVER_API.getComment(new GetSentCommentCallback(mainActivity, commentsRecycler), postId, commentsAdapter.getItemCount(), true, MainActivity.token);
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {

    }

    @Override
    public void fail(Throwable obj)
    {

    }
}
