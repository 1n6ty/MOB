package com.example.mobv2.ui.callbacks;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private AppBarLayout appBar;
    private View dragger;

    public PostsSheetCallback(AppBarLayout appBar, View dragger)
    {
        this.appBar = appBar;
        this.dragger = dragger;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState)
    {
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset)
    {
        appBar.setAlpha(slideOffset);
        dragger.setAlpha((float) (-Math.log(slideOffset) / Math.log(20)));
    }
}
