package com.example.mobv2.ui.callbacks;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private BottomSheetBehavior sheetBehavior;

    private AppBarLayout appBar;
    private Toolbar toolbar;
    private View dragger;

    public PostsSheetCallback(BottomSheetBehavior sheetBehavior, AppBarLayout appBar, Toolbar toolbar, View dragger)
    {
        this.sheetBehavior = sheetBehavior;
        this.appBar = appBar;
        this.toolbar = toolbar;
        this.dragger = dragger;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState)
    {
        switch (newState)
        {
            case BottomSheetBehavior.STATE_COLLAPSED:
                appBar.setVisibility(View.INVISIBLE);
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                appBar.setVisibility(View.GONE);
                break;
            default:
                appBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset)
    {
        appBar.animate()
                .translationY((float) -Math.pow(appBar.getHeight(), 1 - slideOffset))
                .setDuration(0)
                .start();

        dragger.animate()
                .scaleY(2 * (1 - slideOffset))
                .setDuration(0)
                .start();
    }
}
