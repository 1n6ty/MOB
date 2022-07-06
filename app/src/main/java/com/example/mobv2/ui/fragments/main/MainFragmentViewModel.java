package com.example.mobv2.ui.fragments.main;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentViewModel extends ViewModel
{
    private final List<Integer> postIds = new ArrayList<>();
    private boolean mapReady = false;
    private Marker marker;

    public boolean isMapReady()
    {
        return mapReady;
    }

    public void setMapReady(boolean mapReady)
    {
        this.mapReady = mapReady;
    }

    public Marker getMarker()
    {
        return marker;
    }

    public void setMarker(Marker marker)
    {
        this.marker = marker;
    }

    public List<Integer> getPostIds()
    {
        return postIds;
    }

    public void addPostId(int postId)
    {
        postIds.add(postId);
    }
}
