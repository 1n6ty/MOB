package com.example.mobv2.callback.abstraction;

@FunctionalInterface
public interface CommentOkCallback
{
    void createCommentByIdAndAddToCommentIds(String commentId);
}
