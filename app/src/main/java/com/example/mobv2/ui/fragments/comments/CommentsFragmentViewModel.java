package com.example.mobv2.ui.fragments.comments;

import androidx.lifecycle.ViewModel;

import com.example.mobv2.models.Post;

public class CommentsFragmentViewModel extends ViewModel
{
    private Post post;

    public Post getPost()
    {
        return post;
    }

    public void setPost(Post post)
    {
        this.post = post;
    }
}
