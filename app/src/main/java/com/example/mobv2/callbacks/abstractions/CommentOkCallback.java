package com.example.mobv2.callbacks.abstractions;

@FunctionalInterface
public interface CommentOkCallback
{
    void createCommentByIdAndAddToPosts(String commentId);
}
