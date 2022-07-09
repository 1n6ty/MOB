package com.example.mobv2.ui.callbacks;

import android.util.Log;

import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.models.Post;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.google.gson.internal.LinkedTreeMap;

public class GetPostCallback implements MOBServerAPI.MOBAPICallback
{
    private final PostsAdapter postsAdapter;
    private final MainFragmentViewModel viewModel;

    public GetPostCallback(PostsAdapter postsAdapter,
                           MainFragmentViewModel viewModel)
    {
        this.postsAdapter = postsAdapter;
        this.viewModel = viewModel;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        Post post = Post.parseFromMap(response);
        postsAdapter.addPost(post);

        viewModel.setTitleMarker(post.getTitle()); // not work as i wish
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
