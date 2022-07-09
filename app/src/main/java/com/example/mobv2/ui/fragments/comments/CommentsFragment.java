package com.example.mobv2.ui.fragments.comments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.models.Post;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.GetCommentCallback;
import com.example.mobv2.ui.fragments.BaseFragment;

import java.util.ArrayList;

public class CommentsFragment extends BaseFragment<FragmentCommentsBinding>
{
//    private final CommentsFragmentViewModel viewModel;

    private Post post;

    private Toolbar toolbar;
    private RecyclerView commentsRecycler;

    public CommentsFragment(Post post)
    {
        super(R.layout.fragment_comments);
        this.post = post;
//        viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
//        postView = view;
//        viewModel.setPost(post);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();


        initCommentsRecycler();
    }

    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, post.getTitle());
    }

    private void initCommentsRecycler()
    {
        commentsRecycler = binding.commentsRecycler;
        commentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        var commentsAdapter = new CommentsAdapter(new ArrayList<>());
        commentsRecycler.setAdapter(commentsAdapter);

        for (int i = 0; i < post.getCommentsCount(); i++)
        {
            MainActivity.MOB_SERVER_API.getComment(new GetCommentCallback(commentsAdapter), post.getId(), i, true, MainActivity.token);
        }
    }
}
