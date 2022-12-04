package com.example.mobv2.ui.callback;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private final MainActivity mainActivity;
    private final FragmentMainBinding binding;

    public PostsSheetCallback(MainActivity mainActivity,
                              FragmentMainBinding binding)
    {
        this.mainActivity = mainActivity;
        this.binding = binding;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet,
                               int newState)
    {
        var resources = mainActivity.getResources();
        var framePosts = binding.framePosts;
        var postsAppBar = binding.postsAppBar;

        framePosts.setRadius(0);
        switch (newState)
        {
            case BottomSheetBehavior.STATE_COLLAPSED:
                framePosts.setRadius(resources.getDimension(R.dimen.horizontal_margin));
                postsAppBar.setVisibility(View.INVISIBLE);
                mainActivity.getWindow()
                            .setNavigationBarColor(resources.getColor(
                                    mainActivity.getAttribute(R.attr.backgroundSecondaryWindow)));
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                postsAppBar.setVisibility(View.GONE);
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
            case BottomSheetBehavior.STATE_EXPANDED:
                mainActivity.getWindow()
                            .setNavigationBarColor(resources.getColor(
                                    mainActivity.getAttribute(R.attr.backgroundPrimaryWindow)));
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
                postsAppBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet,
                        float slideOffset)
    {
        setPostsAppBarAnimation(slideOffset);
        setBottomAppBarLayoutAnimation(slideOffset);
    }

    private void setPostsAppBarAnimation(float slideOffset)
    {
        var postsAppBar = binding.postsAppBar;
        postsAppBar.animate()
                   .translationY(
                           ((float) -Math.pow(postsAppBar.getHeight(), 1 - slideOffset / 1.5)))
                   .setDuration(0)
                   .start();
    }

    private void setBottomAppBarLayoutAnimation(float slideOffset)
    {
        if (slideOffset > 0)
        {
            var bottomAppbarLayout = binding.bottomAppbarLayout;
            bottomAppbarLayout.animate()
                              .translationY((float) Math.pow(bottomAppbarLayout.getHeight(),
                                      Math.pow(slideOffset, 2)))
                              .setDuration(0)
                              .start();
        }
    }
}
