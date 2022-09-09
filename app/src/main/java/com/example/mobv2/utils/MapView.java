package com.example.mobv2.utils;

import android.database.Observable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;

public class MapView
{
    private final GoogleMap googleMap;
    private Adapter adapter;
    private final MapViewDataObserver observer = new MapViewDataObserver();

    public MapView(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
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
            adapter.registerAdapterDataObserver(observer);

            adapter.onCreate(googleMap);
        }

    }

    public static abstract class Adapter
    {
        private final AdapterDataObservable observable = new AdapterDataObservable();

        public abstract void onCreate(GoogleMap googleMap);

        public abstract void onBindMarker(int position);

        public void bindMarker(int position)
        {
            onBindMarker(position);
        }

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

        public final void notifyItemChanged(int position)
        {
            observable.notifyItemRangeChanged(position, 1);
        }

        public final void notifyItemRangeChanged(int positionStart,
                                                 int itemCount)
        {
            observable.notifyItemRangeChanged(positionStart, itemCount);
        }

        /*public final void notifyItemInserted(int position)
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
        }*/
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

        /*public void notifyItemRangeInserted(int positionStart,
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
        }*/
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

        /*public void onItemRangeInserted(int positionStart,
                                        int itemCount)
        {
            // do nothing
        }

        public void onItemRangeRemoved(int positionStart,
                                       int itemCount)
        {
            // do nothing
        }*/
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
            if (itemCount == 1)
            {
                adapter.bindMarker(positionStart);
                return;
            }

            for (int i = positionStart; i < itemCount; i++)
            {
                adapter.bindMarker(i);
            }
        }

        /*@Override
        public void onItemRangeInserted(int positionStart,
                                        int itemCount)
        {
            super.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart,
                                       int itemCount)
        {
            super.onItemRangeRemoved(positionStart, itemCount);
        }*/
    }
}
