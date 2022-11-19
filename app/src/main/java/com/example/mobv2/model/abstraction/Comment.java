package com.example.mobv2.model.abstraction;

import com.example.mobv2.model.Reaction;

import java.util.Date;
import java.util.List;

public interface Comment
{
    String getId();

    User getUser();

    Date getDate();

    String getText();

    List<Reaction> getReactions();

    List<String> getCommentIds();

    List<String> getPositiveRates();

    List<String> getNegativeRates();
}
