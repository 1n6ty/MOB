package com.example.mobv2.models;

import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

public class MarkerInfo
{
    public static final int ADDRESS_MARKER = 0, SUB_ADDRESS_MARKER = 1;
    private static final String TITLE_DEFAULT = "The mark";

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

    public static class MarkerInfoBuilder implements ParsableFromMap<MarkerInfo>
    {
        private String title;
        private LatLng position;

        @Override
        public MarkerInfo parseFromMap(Map<String, Object> map)
        {
            setTitleByDefault();
            parsePositionFromMap(map);

            return new MarkerInfo(title, position, SUB_ADDRESS_MARKER);
        }

        private void setTitleByDefault()
        {
            title = TITLE_DEFAULT;
        }

        private void parsePositionFromMap(Map<String, Object> map)
        {
            var mark = (LinkedTreeMap<String, Double>) map.get("mark");
            double x = mark.get("x");
            double y = mark.get("y");
            position = new LatLng(x, y);
        }
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
