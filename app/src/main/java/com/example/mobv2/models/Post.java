package com.example.mobv2.models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Post
{
    private Bitmap avatar;
    private User user;
    private Date date;

    private List<Reaction> reactions;

    public Post(Bitmap avatar, User user, Date date, List<Reaction> reactions)
    {
        this.avatar = avatar;
        this.user = user;
        this.date = date;
        this.reactions = reactions;
    }

    public Bitmap getAvatar()
    {
        return avatar;
    }

    public User getUser()
    {
        return user;
    }

    public Date getDate()
    {
        return date;
    }

    public List<Reaction> getReactions()
    {
        return reactions;
    }
}
