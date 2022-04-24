package com.example.mobv2.ui.callbacks;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PostsSheetCallback extends BottomSheetBehavior.BottomSheetCallback
{
    private BottomSheetBehavior sheetBehavior;

    private AppBarLayout appBar;
    private Toolbar toolbar;
    private View dragger;

    public PostsSheetCallback(BottomSheetBehavior sheetBehavior,
                              AppBarLayout appBar,
                              Toolbar toolbar,
                              View dragger)
    {
        this.sheetBehavior = sheetBehavior;
        this.appBar = appBar;
        this.toolbar = toolbar;
        this.dragger = dragger;


//        int totalMargin = (int) getResources().getDimension(R.dimen.total_margin);
//        AppBarLayout.LayoutParams layoutParams =
//                new AppBarLayout.LayoutParams(appBar.getLayoutParams());
//        layoutParams.setMargins(0, (int) dragger.getResources()
//                                                .getDimension(R.dimen.toolbar_rest_margin_top), 0, 0);
//        appBar.setLayoutParams(layoutParams);

//        appBar.set(appBar.getHeight() -  R.dimen.toolbar_rest_margin_top);
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
            default:
                appBar.setVisibility(View.VISIBLE);
                sheetBehavior.setPeekHeight(appBar.getHeight());
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
