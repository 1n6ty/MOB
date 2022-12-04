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

    public float getRotation()
    {
        return marker.getRotation();
    }

    public float getZIndex()
    {
        return marker.getZIndex();
    }

    @NonNull
    public LatLng getPosition()
    {
        return marker.getPosition();
    }

    @Nullable
    public Object getTag()
    {
        return marker.getTag();
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

    @Nullable
    public String getTitle()
    {
        return marker.getTitle();
    }

    public void hideInfoWindow()
    {
        marker.hideInfoWindow();
    }

    public void remove()
    {
        marker.remove();
    }

    public void setAlpha(float alpha)
    {
        marker.setAlpha(alpha);
    }

    public void setAnchor(float anchorU,
                          float anchorV)
    {
        marker.setAnchor(anchorU, anchorV);
    }

    public void setDraggable(boolean draggable)
    {
        marker.setDraggable(draggable);
    }

    public void setFlat(boolean flat)
    {
        marker.setFlat(flat);
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

    public void setPosition(@NonNull LatLng latlng)
    {
        marker.setPosition(latlng);
    }

    public void setRotation(float rotation)
    {
        marker.setRotation(rotation);
    }

    public void setSnippet(@Nullable String snippet)
    {
        marker.setSnippet(snippet);
    }

    public void setTag(@Nullable Object tag)
    {
        marker.setTag(tag);
    }

    public void setTitle(@Nullable String title)
    {
        marker.setTitle(title);
    }

    public void setVisible(boolean visible)
    {
        marker.setVisible(visible);
    }

    public void setZIndex(float zIndex)
    {
        marker.setZIndex(zIndex);
    }

    public void showInfoWindow()
    {
        marker.showInfoWindow();
    }

    public boolean isDraggable()
    {
        return marker.isDraggable();
    }

    public boolean isFlat()
    {
        return marker.isFlat();
    }

    public boolean isInfoWindowShown()
    {
        return marker.isInfoWindowShown();
    }

    public boolean isVisible()
    {
        return marker.isVisible();
    }

    public interface OnMarkerClickListener
    {
        void onMarkerClick(MarkerView markerView);
    }
}
