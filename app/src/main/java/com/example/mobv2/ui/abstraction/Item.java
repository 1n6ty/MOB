package com.example.mobv2.ui.abstraction;

import androidx.annotation.NonNull;

public interface Item<T>
{
    void refreshItemBinding(@NonNull T binding);
}
