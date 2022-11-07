package com.example.mobv2.ui.views;

import android.database.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.adapters.abstractions.AdapterHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapView
{
    private static final MarkerOptions MARKER_OPTIONS_DEFAULT =
            new MarkerOptions().position(new LatLng(0, 0));

    private final GoogleMap googleMap;
    private Adapter adapter;
    private final AdapterHelper adapterHelper;
    private final List<MarkerView> markerViews = new ArrayList<>();

    private final MapViewDataObserver observer = new MapViewDataObserver();

    public MapView(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        adapterHelper = new AdapterHelper(new AdapterHelper.Callback()
        {
            @Override
            public MarkerView findMarkerView(int position)
            {
                return markerViews.get(position);
            }

            @Override
            public void onItemRangeChanged(int positionStart,
                                           int itemCount)
            {
                if (itemCount == 1)
                {
                    adapter.bindMarker(findMarkerView(positionStart), positionStart);
                    return;
                }

                for (int i = positionStart; i < itemCount; i++)
                {
                    adapter.bindMarker(findMarkerView(positionStart), i);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart,
                                            int itemCount)
            {
                if (itemCount == 1)
                {
                    MarkerView markerView =
                            new MarkerView(googleMap.addMarker(MARKER_OPTIONS_DEFAULT));
                    markerViews.add(markerView);
                    adapter.bindMarker(findMarkerView(positionStart), positionStart);
                    return;
                }

                for (int i = positionStart; i < itemCount; i++)
                {
                    MarkerView markerView =
                            new MarkerView(googleMap.addMarker(MARKER_OPTIONS_DEFAULT));
                    markerViews.add(markerView);
                    adapter.bindMarker(findMarkerView(i), i);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart,
                                           int itemCount)
            {
                if (itemCount == 1)
                {
                    markerViews.get(positionStart)
                               .remove();
                    markerViews.remove(positionStart);
                    return;
                }

                Iterator<MarkerView> iterator = markerViews.iterator();
                while (iterator.hasNext())
                {
                    MarkerView nextMarkerView = iterator.next();
                    nextMarkerView.remove();
                    iterator.remove();
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
            setOnMarkerClickListener(new OnMarkerClickListener()
            {
                @Override
                public void onMarkerClick(MarkerView markerView)
                {
                    markerView.onClick();
                }
            });

            adapter.onCreate(this);
        }
    }

    private void onAdapterChanged()
    {
        for (int i = 0; i < adapter.getItemCount(); i++)
        {
            MarkerView markerView = new MarkerView(googleMap.addMarker(MARKER_OPTIONS_DEFAULT));
            markerViews.add(markerView);
        }
    }

    @Nullable
    public Marker addMarker(@NonNull MarkerOptions options)
    {
        return googleMap.addMarker(options);
    }

    public void animateCamera(@NonNull CameraUpdate update)
    {
        googleMap.animateCamera(update);
    }

    public void animateCamera(@NonNull CameraUpdate update,
                              @Nullable GoogleMap.CancelableCallback callback)
    {
        googleMap.animateCamera(update, callback);
    }

    public void animateCamera(@NonNull CameraUpdate update,
                              int durationMs,
                              @Nullable GoogleMap.CancelableCallback callback)
    {
        googleMap.animateCamera(update, durationMs, callback);
    }

    public void clear()
    {
        googleMap.clear();
    }

    public void moveCamera(@NonNull CameraUpdate update)
    {
        googleMap.moveCamera(update);
    }

    public void setOnMapClickListener(@NonNull MapView.OnMapClickListener listener)
    {
        googleMap.setOnMapClickListener(listener);
    }

    public void setOnMapLongClickListener(@NonNull MapView.OnMapLongClickListener listener)
    {
        googleMap.setOnMapLongClickListener(listener);
    }

    protected void setOnMarkerClickListener(@NonNull MapView.OnMarkerClickListener listener)
    {
        googleMap.setOnMarkerClickListener(marker ->
        {
            for (MarkerView markerView: markerViews)
            {
                if (markerView.getId().equals(marker.getId()))
                {
                    listener.onMarkerClick(markerView);
                }
            }
            return true;
        });
    }

    public interface OnMapClickListener extends GoogleMap.OnMapClickListener
    {
        void onMapClick(@NonNull LatLng latLng);
    }

    public interface OnMapLongClickListener extends GoogleMap.OnMapLongClickListener
    {
        void onMapLongClick(@NonNull LatLng latLng);
    }

    public interface OnMarkerClickListener
    {
        void onMarkerClick(MarkerView markerView);
    }

    public static abstract class Adapter
    {
        private final AdapterDataObservable observable = new AdapterDataObservable();

        public abstract void onCreate(MapView mapView);

        public abstract void onBindMarkerView(MarkerView markerView,
                                              int position);

        public void bindMarker(MarkerView markerView,
                               int position)
        {
            onBindMarkerView(markerView, position);
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
