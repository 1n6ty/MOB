package com.example.mobv2.models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Post
{
    public static final int POST_ONLY_TEXT = 0, POST_ONLY_IMAGES = 1, POST_FULL = 2;

    private Bitmap avatar;
    private User user;
    private Date date;
    private String text;
    private List<Bitmap> images;

    private List<Reaction> reactions;

    private int type;

    public Post(Bitmap avatar,
                User user,
                Date date,
                String text,
                List<Reaction> reactions)
    {
        this(avatar, user, date, text, null, reactions);
        type = POST_ONLY_TEXT;
    }

    public Post(Bitmap avatar,
                User user,
                Date date,
                List<Bitmap> images,
                List<Reaction> reactions)
    {
        this(avatar, user, date, null, images, reactions);
        type = POST_ONLY_IMAGES;
    }

    public Post(Bitmap avatar,
                User user,
                Date date,
                String text,
                List<Bitmap> images,
                List<Reaction> reactions)
    {
        this.avatar = avatar;
        this.user = user;
        this.date = date;
        this.text = text;
        this.images = images;
        this.reactions = reactions;
        type = POST_FULL;
    }

    public Bitmap getAvatar()
    {
        return avatar;
    }

    public User getUser()
    {
        return user;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Date getDate()
    {
        return date;
    }

    public List<Reaction> getReactions()
    {
        return reactions;
    }

    public int getType()
    {
        return type;
    }
}
