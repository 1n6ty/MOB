package com.example.mobv2.ui.callbacks;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.mobv2.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private final AppBarLayout appBar;
    private final View dragger;

    public PostsSheetCallback(CoordinatorLayout coordinatorLayout)
    {
        appBar = coordinatorLayout.findViewById(R.id.posts_app_bar);
        dragger = coordinatorLayout.findViewById(R.id.dragger);
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet,
                               int newState)
    {
        switch (newState)
        {
            case BottomSheetBehavior.STATE_COLLAPSED:
                appBar.setVisibility(View.INVISIBLE);
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                appBar.setVisibility(View.GONE);
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
            case BottomSheetBehavior.STATE_EXPANDED:
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
                appBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet,
                        float slideOffset)
    {
        appBar.animate()
              .translationY(((float) -Math.pow(appBar.getHeight(), 1 - slideOffset / 1.5)))
              .setDuration(0)
              .start();

        dragger.animate()
               .scaleY(2 * (1 - slideOffset))
               .setDuration(0)
               .start();
    }
}
