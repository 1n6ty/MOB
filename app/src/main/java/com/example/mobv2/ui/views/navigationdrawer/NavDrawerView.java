package com.example.mobv2.ui.views.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

public class NavDrawerView extends NavigationView implements NavigationView.OnNavigationItemSelectedListener
{
    private OnNavigationItemSelectedListener listener;

    public NavDrawerView(@NonNull Context context, AttributeSet attrs)
    {
        super(context, attrs);

        super.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener)
    {
        this.listener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if (listener != null)
        {
            boolean isPossible = listener.onNavigationItemSelected(item);

            return isPossible;
        }

        return false;
    }
}
