package com.example.mobv2.utils;

import android.database.Observable;

import androidx.annotation.NonNull;

import com.example.mobv2.adapters.abstractions.AdapterHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapView
{
    private static final MarkerOptions MARKER_OPTIONS_DEFAULT =
            new MarkerOptions().position(new LatLng(0, 0));

    private final GoogleMap googleMap;
    private Adapter adapter;
    private final AdapterHelper adapterHelper;
    private final List<Marker> markers = new ArrayList<>();

    private final MapViewDataObserver observer = new MapViewDataObserver();

    public MapView(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        adapterHelper = new AdapterHelper(new AdapterHelper.Callback()
        {
            @Override
            public Marker findMarker(int position)
            {
                return markers.get(position);
            }

            @Override
            public void onItemRangeChanged(int positionStart,
                                           int itemCount)
            {
                if (itemCount == 1)
                {
                    adapter.bindMarker(findMarker(positionStart), positionStart);
                    return;
                }

                for (int i = positionStart; i < itemCount; i++)
                {
                    adapter.bindMarker(findMarker(positionStart), i);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart,
                                            int itemCount)
            {
                if (itemCount == 1)
                {
                    Marker marker = googleMap.addMarker(MARKER_OPTIONS_DEFAULT);
                    markers.add(marker);
                    adapter.bindMarker(findMarker(positionStart), positionStart);
                    return;
                }

                for (int i = positionStart; i < itemCount; i++)
                {
                    Marker marker = googleMap.addMarker(MARKER_OPTIONS_DEFAULT);
                    markers.add(marker);
                    adapter.bindMarker(findMarker(i), i);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart,
                                           int itemCount)
            {
                if (itemCount == 1)
                {
                    markers.get(positionStart)
                           .remove();
                    adapter.bindMarker(findMarker(positionStart), positionStart);
                    return;
                }

                for (int i = positionStart; i < itemCount; i++)
                {
                    markers.get(i)
                           .remove();
                    adapter.bindMarker(findMarker(i), i);
                }
            }
        });
    }

    public Adapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(Adapter adapter)
    {
        if (this.adapter != null)
            this.adapter.unregisterAdapterDataObserver(observer);

        this.adapter = adapter;
        if (adapter != null)
        {
            onAdapterChanged();
            adapter.registerAdapterDataObserver(observer);

            adapter.onCreate(googleMap);
        }
    }

    private void onAdapterChanged()
    {
        for (int i = 0; i < adapter.getItemCount(); i++)
        {
            Marker marker = googleMap.addMarker(MARKER_OPTIONS_DEFAULT);
            markers.add(marker);
        }
    }

    public static abstract class Adapter
    {
        private final AdapterDataObservable observable = new AdapterDataObservable();

        public abstract void onCreate(GoogleMap googleMap);

        public abstract void onBindMarker(Marker marker,
                                          int position);

        public void bindMarker(Marker marker,
                               int position)
        {
            onBindMarker(marker, position);
        }

        public abstract int getItemCount();

        public final boolean hasObservers()
        {
            return observable.hasObservers();
        }

        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer)
        {
            observable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer)
        {
            observable.unregisterObserver(observer);
        }

        public final void notifyDataSetChanged()
        {
            observable.notifyChanged();
        }

        public final void notifyItemChanged(int positionStart)
        {
            observable.notifyItemRangeChanged(positionStart, 1);
        }

        public final void notifyItemRangeChanged(int positionStart,
                                                 int itemCount)
        {
            observable.notifyItemRangeChanged(positionStart, itemCount);
        }

        public final void notifyItemInserted(int position)
        {
            observable.notifyItemRangeInserted(position, 1);
        }

        public final void notifyItemRangeInserted(int positionStart,
                                                  int itemCount)
        {
            observable.notifyItemRangeInserted(positionStart, itemCount);
        }

        public final void notifyItemRemoved(int position)
        {
            observable.notifyItemRangeRemoved(position, 1);
        }

        public final void notifyItemRangeRemoved(int positionStart,
                                                 int itemCount)
        {
            observable.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    private static class AdapterDataObservable extends Observable<AdapterDataObserver>
    {
        public boolean hasObservers()
        {
            return !mObservers.isEmpty();
        }

        public void notifyChanged()
        {
            for (int i = mObservers.size() - 1; i >= 0; i--)
            {
                mObservers.get(i)
                          .onChanged();
            }
        }

        public void notifyItemRangeChanged(int positionStart,
                                           int itemCount)
        {
            for (int i = mObservers.size() - 1; i >= 0; i--)
            {
                mObservers.get(i)
                          .onItemRangeChanged(positionStart, itemCount);
            }
        }

        public void notifyItemRangeInserted(int positionStart,
                                            int itemCount)
        {
            for (int i = mObservers.size() - 1; i >= 0; i--)
            {
                mObservers.get(i)
                          .onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart,
                                           int itemCount)
        {
            for (int i = mObservers.size() - 1; i >= 0; i--)
            {
                mObservers.get(i)
                          .onItemRangeRemoved(positionStart, itemCount);
            }
        }
    }

    public abstract static class AdapterDataObserver
    {
        public void onChanged()
        {
            // do nothing
        }

        public void onItemRangeChanged(int positionStart,
                                       int itemCount)
        {
            // do nothing
        }

        public void onItemRangeInserted(int positionStart,
                                        int itemCount)
        {
            // do nothing
        }

        public void onItemRangeRemoved(int positionStart,
                                       int itemCount)
        {
            // do nothing
        }
    }

    private class MapViewDataObserver extends AdapterDataObserver
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart,
                                       int itemCount)
        {
            adapterHelper.onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart,
                                        int itemCount)
        {
            adapterHelper.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart,
                                       int itemCount)
        {
            adapterHelper.onItemRangeRemoved(positionStart, itemCount);
        }
    }
}
