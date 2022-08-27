package com.example.mobv2.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class BitmapConverter
{
    public static BitmapDescriptor drawableToBitmapDescriptor(Resources resources,
                                                              int id)
    {
        return BitmapDescriptorFactory.fromBitmap(getBitmap(resources.getDrawable(id)));
    }

    public static Bitmap drawableToBitmap(Resources resources,
                                          @DrawableRes int id)
    {
        return getBitmap(resources.getDrawable(id));
    }

    private static Bitmap getBitmap(Drawable vectorDrawable)
    {
        if (vectorDrawable != null)
        {
            int width = vectorDrawable.getIntrinsicWidth();
            int height = vectorDrawable.getIntrinsicHeight();

            vectorDrawable.setBounds(0, 0, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);

            return bitmap;
        }

        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap bm,
                                          int newWidth,
                                          int newHeight)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
