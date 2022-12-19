package com.example.mobv2.ui.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerView
{
    private final Marker marker;
    private OnMarkerClickListener onMarkerClickListener;
    private Map.OnMarkerDragListener onMarkerDragListener;

    public MarkerView(Marker marker)
    {
        this.marker = marker;
    }

    public void setOnClickListener(OnMarkerClickListener onMarkerClickListener)
    {
        this.onMarkerClickListener = onMarkerClickListener;
    }

    public void setOnDragListener(Map.OnMarkerDragListener onMarkerDragListener)
    {
        this.onMarkerDragListener = onMarkerDragListener;
    }

    public void onClick()
    {
        onMarkerClickListener.onMarkerClick(this);
    }

    public void onMarkerDragEnd()
    {
        onMarkerDragListener.onMarkerDragEnd(this);
    }

    public float getAlpha()
    {
        return marker.getAlpha();
    }

    public void setAlpha(float alpha)
    {
        marker.setAlpha(alpha);
    }

    public float getRotation()
    {
        return marker.getRotation();
    }

    public void setRotation(float rotation)
    {
        marker.setRotation(rotation);
    }

    public float getZIndex()
    {
        return marker.getZIndex();
    }

    public void setZIndex(float zIndex)
    {
        marker.setZIndex(zIndex);
    }

    @NonNull
    public LatLng getPosition()
    {
        return marker.getPosition();
    }

    public void setPosition(@NonNull LatLng latlng)
    {
        marker.setPosition(latlng);
    }

    @Nullable
    public Object getTag()
    {
        return marker.getTag();
    }

    public void setTag(@Nullable Object tag)
    {
        marker.setTag(tag);
    }

    @NonNull
    public String getId()
    {
        return marker.getId();
    }

    @Nullable
    public String getSnippet()
    {
        return marker.getSnippet();
    }

    public void setSnippet(@Nullable String snippet)
    {
        marker.setSnippet(snippet);
    }

    @Nullable
    public String getTitle()
    {
        return marker.getTitle();
    }

    public void setTitle(@Nullable String title)
    {
        marker.setTitle(title);
    }

    public void hideInfoWindow()
    {
        marker.hideInfoWindow();
    }

    public void remove()
    {
        marker.remove();
    }

    public void setAnchor(float anchorU,
                          float anchorV)
    {
        marker.setAnchor(anchorU, anchorV);
    }

    public void setIcon(@Nullable BitmapDescriptor iconDescriptor)
    {
        marker.setIcon(iconDescriptor);
    }

    public void setInfoWindowAnchor(float anchorU,
                                    float anchorV)
    {
        marker.setInfoWindowAnchor(anchorU, anchorV);
    }

    public void showInfoWindow()
    {
        marker.showInfoWindow();
    }

    public boolean isDraggable()
    {
        return marker.isDraggable();
    }

    public void setDraggable(boolean draggable)
    {
        marker.setDraggable(draggable);
    }

    public boolean isFlat()
    {
        return marker.isFlat();
    }

    public void setFlat(boolean flat)
    {
        marker.setFlat(flat);
    }

    public boolean isInfoWindowShown()
    {
        return marker.isInfoWindowShown();
    }

    public boolean isVisible()
    {
        return marker.isVisible();
    }

    public void setVisible(boolean visible)
    {
        marker.setVisible(visible);
    }

    public interface OnMarkerClickListener
    {
        void onMarkerClick(MarkerView markerView);
    }
}
