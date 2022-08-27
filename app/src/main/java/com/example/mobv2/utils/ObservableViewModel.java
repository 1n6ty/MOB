package com.example.mobv2.utils;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

public class ObservableViewModel extends ViewModel implements Observable
{
    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback)
    {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback)
    {
        callbacks.remove(callback);
    }

    protected void notifyChange()
    {
        callbacks.notifyCallbacks(this, 0, null);
    }

    protected void notifyPropertyChanged(int fieldId)
    {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}
