package com.example.mobv2.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MarkerInfo
{
    public static final int ADDRESS_MARKER = 0, SUB_ADDRESS_MARKER = 1;

    private String id;

    private String title;
    private LatLng position;
    private Object tag;

    private final int markerType;
    private final Map<String, Object> metadata;
    private boolean clicked;

    public MarkerInfo(String title,
                      LatLng position,
                      int markerType)
    {
        this.title = title;
        this.position = position;
        this.markerType = markerType;

        metadata = new HashMap<>();
        tag = null;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public LatLng getPosition()
    {
        return position;
    }

    public Object getTag()
    {
        return tag;
    }

    public int getMarkerType()
    {
        return markerType;
    }

    public Map<String, Object> getMetadata()
    {
        return metadata;
    }

    public boolean isClicked()
    {
        return clicked;
    }

    public void setClicked(boolean clicked)
    {
        this.clicked = clicked;
    }
}
