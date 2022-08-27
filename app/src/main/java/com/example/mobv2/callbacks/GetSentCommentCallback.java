package com.example.mobv2.callbacks;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragmentViewModel;
import com.google.gson.internal.LinkedTreeMap;

public class GetSentCommentCallback extends GetCommentCallback
{
    public GetSentCommentCallback(MainActivity mainActivity,
                                  RecyclerView commentsRecycler)
    {
        super(mainActivity, commentsRecycler, false);
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        super.funcOk(obj);
        var commentsAdapter = (CommentsAdapter) commentsRecycler.getAdapter();
        commentsRecycler.smoothScrollToPosition(commentsAdapter.getItemCount() - 1);

        var commentsFragmentViewModel =
                new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
        commentsFragmentViewModel.setCommentsCount(commentsFragmentViewModel.getCommentsCount() + 1);
    }
}
