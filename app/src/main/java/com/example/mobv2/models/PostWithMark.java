package com.example.mobv2.models;

import com.google.android.gms.maps.model.LatLng;

public class PostWithMark
{
    private final double x;
    private final double y;
    private final int postId;

    public PostWithMark(double x,
                        double y,
                        int postId)
    {
        this.x = x;
        this.y = y;
        this.postId = postId;
    }

    public LatLng getPosition()
    {
        return new LatLng(x, y);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public int getPostId()
    {
        return postId;
    }
}
