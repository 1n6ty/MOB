package com.example.mobv2.ui.callbacks;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.mobv2.R;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private final MainActivity mainActivity;
    private final AppBarLayout appBar;
    private final View dragger;
    private final MaterialCardView materialCardView;

    public PostsSheetCallback(MainActivity mainActivity,
                              MaterialCardView materialCardView)
    {
        this.mainActivity = mainActivity;
        this.materialCardView = materialCardView;
        appBar = materialCardView.findViewById(R.id.posts_app_bar);
        dragger = materialCardView.findViewById(R.id.dragger);
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet,
                               int newState)
    {
        materialCardView.setRadius(0);

        switch (newState)
        {
            case BottomSheetBehavior.STATE_COLLAPSED:
                materialCardView.setRadius(mainActivity.getResources().getDimension(R.dimen.horizontal_margin));
                appBar.setVisibility(View.INVISIBLE);
                mainActivity.getWindow()
                            .setNavigationBarColor(mainActivity.getResources()
                                                               .getColor(mainActivity.getAttribute(R.attr.backgroundSecondaryWindow)));
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                appBar.setVisibility(View.GONE);
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
            case BottomSheetBehavior.STATE_EXPANDED:
                mainActivity.getWindow()
                            .setNavigationBarColor(mainActivity.getResources()
                                                               .getColor(mainActivity.getAttribute(R.attr.backgroundPrimaryWindow)));
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
