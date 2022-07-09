package com.example.mobv2.ui.fragments.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobv2.models.PostWithMark;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentViewModel extends ViewModel
{
    private final List<PostWithMark> postsWithMarks = new ArrayList<>();
    private boolean mapReady = false;
    private Marker marker;
    private final MutableLiveData<String> titleMarker = new MutableLiveData<>();
    private boolean addressMarker;

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

    public LiveData<String> getTitleMarker()
    {
        return titleMarker;
    }

    public void setTitleMarker(String titleMarker)
    {
        this.titleMarker.setValue(titleMarker);
    }

    public List<PostWithMark> getPostsWithMarks()
    {
        return postsWithMarks;
    }

    public void addPostWithMark(PostWithMark value)
    {
        postsWithMarks.add(value);
    }

    public boolean isAddressMarker()
    {
        return addressMarker;
    }

    public void setAddressMarker(boolean addressMarker)
    {
        this.addressMarker = addressMarker;
    }
}
