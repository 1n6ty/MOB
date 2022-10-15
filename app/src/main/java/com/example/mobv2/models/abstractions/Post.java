package com.example.mobv2.models.abstractions;

import com.example.mobv2.models.Reaction;

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

    List<String> getCommentsIds();

    List<String> getPositiveRates();

    List<String> getNegativeRates();
}
