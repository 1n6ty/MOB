package com.example.mobv2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.models.abstractions.MarkerInfo;
import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

import localdatabase.typeconverters.LatLngConverter;

@Entity
public class MarkerInfoImpl implements MarkerInfo
{
    @Ignore
    public static final int ADDRESS_MARKER = 0, SUB_ADDRESS_MARKER = 1;
    @Ignore
    private static final String TITLE_DEFAULT = "The mark";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "markerinfoid")
    private String id;

    private String title;
    @TypeConverters(LatLngConverter.class)
    private LatLng latLng;

    private String postId;
    private int markerType;

    @Ignore
    private boolean clicked;

    @Ignore
    public MarkerInfoImpl()
    {
    }

    @Ignore
    public MarkerInfoImpl(LatLng latLng,
                          int markerType)
    {
        this(TITLE_DEFAULT, latLng, markerType);
    }

    @Ignore
    public MarkerInfoImpl(String title,
                          LatLng latLng,
                          int markerType)
    {
        this.title = title;
        this.latLng = latLng;
        this.markerType = markerType;
    }

    public MarkerInfoImpl(String title,
                          LatLng latLng,
                          String postId,
                          int markerType)
    {
        this.title = title;
        this.latLng = latLng;
        this.postId = postId;
        this.markerType = markerType;
    }

    @Override
    public boolean compareById(MarkerInfo markerInfo)
    {
        if (markerInfo == null) return false;

        return id.equals(markerInfo.getId());
    }

    public static class MarkerInfoBuilder implements ParsableFromMap<MarkerInfoImpl>
    {
        private String title;
        private LatLng position;

        @NonNull
        @Override
        public MarkerInfoImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            setTitleByDefault();
            parsePositionFromMap(map);

            return new MarkerInfoImpl(title, position, SUB_ADDRESS_MARKER);
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

    public LatLng getLatLng()
    {
        return latLng;
    }

    public int getMarkerType()
    {
        return markerType;
    }

    public void setPostId(String postId)
    {
        this.postId = postId;
    }

    public String getPostId()
    {
        return postId;
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
