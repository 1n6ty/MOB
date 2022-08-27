package com.example.mobv2.callbacks;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.models.Post;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.google.gson.internal.LinkedTreeMap;

public class GetPostCallback implements MOBServerAPI.MOBAPICallback
{
    private final RecyclerView postsRecycler;

    public GetPostCallback(RecyclerView postsRecycler)
    {
        this.postsRecycler = postsRecycler;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        Post post = Post.parseFromMap(response);
        var postsAdapter = (PostsAdapter) postsRecycler.getAdapter();
        postsAdapter.addPost(post);
        postsRecycler.scrollToPosition(0);
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
//                    Toast.makeText(getContext(), "Post is not available", Toast.LENGTH_LONG)
//                         .show();
    }

    @Override
    public void fail(Throwable obj)
    {
        Log.v("DEBUG", obj.toString());
//                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_LONG)
//                         .show();
    }
}
