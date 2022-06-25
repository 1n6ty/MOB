package com.example.mobv2.ui.fragments.main;

import androidx.lifecycle.ViewModel;

public class MainFragmentViewModel extends ViewModel
{
    private boolean mapReady = false;

    public boolean isMapReady()
    {
        return mapReady;
    }

    public void setMapReady(boolean mapReady)
    {
        this.mapReady = mapReady;
    }
}
