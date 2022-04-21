package com.example.mobv2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class BitmapConverter
{
    public static BitmapDescriptor drawableToBitmapDescriptor(Context context,
                                                              int id)
    {
        Drawable vectorDrawable;
        vectorDrawable = AppCompatResources.getDrawable(context, id);
        if (vectorDrawable != null)
        {
            int w = vectorDrawable.getIntrinsicWidth();
            int h = vectorDrawable.getIntrinsicHeight();

            vectorDrawable.setBounds(0, 0, w, h);
            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);
        }
        return null;
    }
}
