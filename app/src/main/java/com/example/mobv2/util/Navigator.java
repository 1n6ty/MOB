package com.example.mobv2.util;

import android.view.View;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public class Navigator
{
    private final MainActivity mainActivity;
    private final View navigationContentFrame;

    private Map<String, Fragment> fragmentHashMap = new HashMap<>();

    public Navigator(MainActivity mainActivity,
                     View navigationContentFrame)
    {
        this.mainActivity = mainActivity;
        this.navigationContentFrame = navigationContentFrame;
    }

    public void goToFragment(@NonNull Fragment fragment)
    {
        goToFragment(fragment, R.animator.slide_in_left);
    }

    public void goToFragment(@NonNull Fragment fragment,
                             @AnimatorRes int enter)
    {
        goToFragment(fragment, enter, R.animator.slide_in_right);
    }

    public void goToFragment(@NonNull Fragment fragment,
                             @AnimatorRes int enter,
                             @AnimatorRes int exit)
    {
        String fragmentName = fragment.getClass().getSimpleName();

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(enter, exit, exit, exit)
                    .add(navigationContentFrame.getId(), fragment, fragmentName)
                    .runOnCommit(() -> fragmentHashMap.put(fragmentName, fragment))
                    .commit();
    }

    public void replaceFragment(@NonNull Fragment fragment)
    {
        replaceFragment(fragment, R.animator.slide_in_left);
    }

    public void replaceFragment(@NonNull Fragment fragment,
                                @AnimatorRes int enter)
    {
        String fragmentName = fragment.getClass().getSimpleName();

        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(enter, android.R.animator.fade_out)
                    .replace(navigationContentFrame.getId(), fragment, fragmentName)
                .runOnCommit(() -> fragmentHashMap.replace(fragmentName, fragment))
                    .commit();
    }

    public void toPreviousFragment()
    {
        mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_in_right)
                    .remove(mainActivity.getFragmentAtFrame())
                    .commitNow();

        ((BaseFragment) mainActivity.getFragmentAtFrame()).update();
    }
}
