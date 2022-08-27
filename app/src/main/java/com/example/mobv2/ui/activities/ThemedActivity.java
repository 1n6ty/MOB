package com.example.mobv2.ui.activities;

import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;

public class ThemedActivity extends AppCompatActivity
{
    public int getAttribute(@AttrRes int resId)
    {
        var typedValue = new TypedValue();
        var successful = getTheme().resolveAttribute(resId, typedValue, true);
        return successful ? typedValue.resourceId : 0;
    }
}