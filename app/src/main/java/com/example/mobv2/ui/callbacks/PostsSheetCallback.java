package com.example.mobv2.ui.callbacks;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.mobv2.R;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private final MainActivity mainActivity;
    private final AppBarLayout postsAppBar;
    private final MaterialCardView materialCardView;
    private final LinearLayout bottomAppbarLayout;

    public PostsSheetCallback(MainActivity mainActivity,
                              MaterialCardView materialCardView,
                              LinearLayout bottomAppbarLayout)
    {
        this.mainActivity = mainActivity;
        this.materialCardView = materialCardView;
        this.bottomAppbarLayout = bottomAppbarLayout;

        this.postsAppBar = materialCardView.findViewById(R.id.posts_app_bar);
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet,
                               int newState)
    {
        materialCardView.setRadius(0);

        switch (newState)
        {
            case BottomSheetBehavior.STATE_COLLAPSED:
                materialCardView.setRadius(mainActivity.getResources()
                                                       .getDimension(R.dimen.horizontal_margin));
                postsAppBar.setVisibility(View.INVISIBLE);
                mainActivity.getWindow()
                            .setNavigationBarColor(mainActivity.getResources()
                                                               .getColor(mainActivity.getAttribute(R.attr.backgroundSecondaryWindow)));
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                postsAppBar.setVisibility(View.GONE);
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
            case BottomSheetBehavior.STATE_EXPANDED:
                mainActivity.getWindow()
                            .setNavigationBarColor(mainActivity.getResources()
                                                               .getColor(mainActivity.getAttribute(R.attr.backgroundPrimaryWindow)));
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
        postsAppBar.animate()
                   .translationY(((float) -Math.pow(postsAppBar.getHeight(), 1 - slideOffset / 1.5)))
                   .setDuration(0)
                   .start();
    }

    private void setBottomAppBarLayoutAnimation(float slideOffset)
    {
        if (slideOffset > 0)
            bottomAppbarLayout.animate()
                              .translationY((float) Math.pow(bottomAppbarLayout.getHeight(), Math.pow(slideOffset, 2)))
                              .setDuration(0)
                              .start();
    }
}
