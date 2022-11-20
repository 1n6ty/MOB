package com.example.mobv2.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.model.abstraction.MarkerInfo;
import com.example.mobv2.util.abstraction.ParsableFromMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

import localDatabase.typeConverter.LatLngConverter;

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

    private String addressId;
    private int markerType;

    @Ignore
    private boolean clicked;

    @Ignore
    public MarkerInfoImpl()
    {
    }

    @Ignore
    public MarkerInfoImpl(String id,
                          LatLng latLng,
                          int markerType)
    {
        this(id, TITLE_DEFAULT, latLng, markerType);
    }

    @Ignore
    public MarkerInfoImpl(String id,
                          String title,
                          LatLng latLng,
                          int markerType)
    {
        this.id = id;
        this.title = title;
        this.latLng = latLng;
        this.markerType = markerType;
    }

    public MarkerInfoImpl(String id,
                          String title,
                          LatLng latLng,
                          String addressId,
                          int markerType)
    {
        this.id = id;
        this.title = title;
        this.latLng = latLng;
        this.addressId = addressId;
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
        private String id;
        private String title;
        private LatLng position;

        @NonNull
        @Override
        public MarkerInfoImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            parseIdFromMap(map);
            setTitleByDefault();
            parsePositionFromMap(map);

            return new MarkerInfoImpl(id, title, position, SUB_ADDRESS_MARKER);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = (String) map.get("post_id");
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

    public String getTitle()
    {
        return title;
    }

    public LatLng getLatLng()
    {
        return latLng;
    }

    @Ignore
    public void setLatLng(LatLng latLng)
    {
        this.latLng = latLng;
    }

    public int getMarkerType()
    {
        return markerType;
    }

    public String getAddressId()
    {
        return addressId;
    }

    public void setAddressId(String addressId)
    {
        this.addressId = addressId;
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
