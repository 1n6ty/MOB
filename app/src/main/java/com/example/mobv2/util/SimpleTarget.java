package com.example.mobv2.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class SimpleTarget extends CustomTarget<Bitmap>
{

    @Override
    public void onResourceReady(@NonNull Bitmap resource,
                                @Nullable Transition<? super Bitmap> transition)
    {
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder)
    {

    }
}
