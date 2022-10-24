package com.example.mobv2.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class MyObservableArrayList<T> extends ArrayList<T>
{
    private OnListChangedCallback<T> callback;

    public MyObservableArrayList()
    {
    }

    public MyObservableArrayList(@NonNull Collection<? extends T> c)
    {
        super(c);
    }

    public void setOnListChangedCallback(OnListChangedCallback<T> callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean add(T t)
    {
        boolean add = super.add(t);
        if (add) if (callback != null) callback.onAdded(t);
        return add;
    }

    @Override
    public void add(int index,
                    T element)
    {
        if (callback != null) callback.onAdded(index, element);
        super.add(index, element);
    }

    @Override
    public T remove(int index)
    {
        if (callback != null) callback.onRemoved(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(@Nullable Object o)
    {
        boolean remove = super.remove(o);
        if (remove) if (callback != null) callback.onRemoved(o);
        return remove;
    }

    public interface OnListChangedCallback<T>
    {
        void onAdded(T t);

        void onAdded(int index,
                     T element);

        void onRemoved(int index);

        void onRemoved(@Nullable Object o);
    }
}
