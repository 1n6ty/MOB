package com.example.mobv2.adapters.abstractions;

import com.google.android.gms.maps.model.Marker;

public class AdapterHelper
{
    private final Callback callback;

    public AdapterHelper(Callback callback)
    {
        this.callback = callback;
    }

    public void onItemRangeChanged(int positionStart,
                                   int itemCount)
    {
        if (callback != null) callback.onItemRangeChanged(positionStart, itemCount);
    }

    public void onItemRangeInserted(int positionStart,
                                    int itemCount)
    {
        if (callback != null) callback.onItemRangeInserted(positionStart, itemCount);
    }

    public void onItemRangeRemoved(int positionStart,
                                   int itemCount)
    {
        if (callback != null) callback.onItemRangeRemoved(positionStart, itemCount);
    }

    public interface Callback
    {
        Marker findMarker(int position);

        void onItemRangeChanged(int positionStart,
                                int itemCount);

        void onItemRangeInserted(int positionStart,
                                 int itemCount);

        void onItemRangeRemoved(int positionStart,
                                int itemCount);
    }
}
