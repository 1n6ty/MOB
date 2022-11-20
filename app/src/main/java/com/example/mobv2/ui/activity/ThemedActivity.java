package com.example.mobv2.ui.activity;

import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ThemedActivity extends AppCompatActivity
{
    public int getAttribute(@AttrRes int resId)
    {
        var typedValue = new TypedValue();
        var successful = getTheme().resolveAttribute(resId, typedValue, true);
        return successful ? typedValue.resourceId : 0;
    }

    @ColorInt
    public int getAttributeColor(@AttrRes int resId)
    {
        return ResourcesCompat.getColor(getResources(), getAttribute(resId), getTheme());
    }

    public int getAttributeColorWithAlpha(@AttrRes int resId, float ratio)
    {
        return getColorWithAlpha(getAttributeColor(resId), ratio);
    }

    public int getColorWithAlpha(@ColorInt int color, float ratio) {
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(alpha, r, g, b);
    }
}