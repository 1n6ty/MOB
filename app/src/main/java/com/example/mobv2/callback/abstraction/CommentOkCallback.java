package com.example.mobv2.callback.abstraction;

@FunctionalInterface
public interface CommentOkCallback
{
    void createCommentByIdAndTextAndAddToCommentIds(String commentId, String messageText);
}
