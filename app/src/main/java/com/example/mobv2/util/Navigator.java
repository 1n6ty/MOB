package com.example.mobv2.util;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;

import java.lang.ref.WeakReference;

public class Navigator
{
    private static MainActivity mainActivity;
    private static WeakReference<View> navigationContentFrame;

    public static void create(MainActivity mainActivity,
                              View navigationContentFrame)
    {
        Navigator.mainActivity = mainActivity;
        Navigator.navigationContentFrame = new WeakReference<>(navigationContentFrame);
    }

    public static void goToFragment(@NonNull Fragment fragment)
    {
        if (checkIfActivityOrNavContentFrameAreNull())
        {
            return;
        }

        String fragmentName = fragment.getClass().getSimpleName();

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(navigationContentFrame.get().getId(), fragment, fragmentName)
                    .commitNow();
    }

    public static void goToFragmentWithSharedElement(@NonNull Fragment fragment,
                                                     View sharedElement,
                                                     String destinationId)
    {
        if (checkIfActivityOrNavContentFrameAreNull())
        {
            return;
        }

        String fragmentName = fragment.getClass().getSimpleName();

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(sharedElement, destinationId)
                    .add(navigationContentFrame.get().getId(), fragment, fragmentName)
                    .commit();
    }

    public static void replaceFragment(@NonNull Fragment fragment)
    {
        if (checkIfActivityOrNavContentFrameAreNull())
        {
            return;
        }

        String fragmentName = fragment.getClass().getSimpleName();

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(navigationContentFrame.get().getId(), fragment, fragmentName)
                    .commit();
    }

    public static void toPreviousFragment()
    {
        if (checkIfActivityOrNavContentFrameAreNull())
        {
            return;
        }

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mainActivity.getFragmentAtFrame())
                    .commitNow();

        ((BaseFragment) mainActivity.getFragmentAtFrame()).update();
    }

    private static boolean checkIfActivityOrNavContentFrameAreNull()
    {
        return mainActivity == null || navigationContentFrame == null;
    }
}
