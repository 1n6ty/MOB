package com.example.mobv2.adapters;

import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.models.PostWithMark;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class MarkersAdapter
{
    private MainActivity mainActivity;
    private GoogleMap googleMap;
    private List<PostWithMark> postWithMarks;

    public MarkersAdapter(MainActivity mainActivity,
                          GoogleMap googleMap,
                          List<PostWithMark> postWithMarks)
    {
        this.mainActivity = mainActivity;
        this.googleMap = googleMap;
        this.postWithMarks = postWithMarks;
    }

    public void addMarker(MarkerInfo markerInfo)
    {

    }
}
