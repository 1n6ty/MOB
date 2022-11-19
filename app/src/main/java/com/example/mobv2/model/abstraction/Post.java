package com.example.mobv2.model.abstraction;

import com.example.mobv2.model.Reaction;

import java.util.Date;
import java.util.List;

public interface Post
{
    String getId();

    User getUser();

    Date getDate();

    String getTitle();

    String getText();

    List<String> getImages();

    List<Reaction> getReactions();

    List<String> getCommentIds();

    List<String> getPositiveRates();

    List<String> getNegativeRates();
}
