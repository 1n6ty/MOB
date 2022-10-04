package com.example.mobv2.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.ui.abstractions.HasToolbar;

import java.util.List;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding>
        implements HasToolbar, ViewSwitcher.ViewFactory, GestureDetector.OnGestureListener
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private Toolbar toolbar;

    private List<Image> images;
    private ImageSwitcher imageSwitcherView;
    private GestureDetector gestureDetector;

    public ImageViewerFragment(List<Image> images)
    {
        super(R.layout.fragment_image_viewer);
        this.images = images;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initImageView();
    }

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_VISIBLE, getResources().getColor(R.color.black));
    }

    private void initImageView()
    {
        imageSwitcherView = binding.imageSwitcher;
        imageSwitcherView.setFactory(this);

        Animation inAnimation = new AlphaAnimation(0, 1);
        inAnimation.setDuration(2000);
        Animation outAnimation = new AlphaAnimation(1, 0);
        outAnimation.setDuration(2000);

        imageSwitcherView.setInAnimation(inAnimation);
        imageSwitcherView.setOutAnimation(outAnimation);

        Image startImage = images.get(0);

        if (startImage.getType() == Image.IMAGE_ONLINE)
        {
//            MainActivity.loadImageInView((String) startImage.getPath(), getView(), imageSwitcherView);
        }
        else if (startImage.getType() == Image.IMAGE_OFFLINE)
        {
            imageSwitcherView.setImageURI((Uri) startImage.getPath());
        }


        gestureDetector = new GestureDetector(mainActivity, this);
    }

    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, images.get(0)
                                         .getName());
    }

    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1,
                            MotionEvent e2,
                            float distanceX,
                            float distanceY)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {

    }

    @Override
    public boolean onFling(MotionEvent e1,
                           MotionEvent e2,
                           float velocityX,
                           float velocityY)
    {
        try
        {
            /*if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // справа налево
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                setPositionNext();
                mImageSwitcher.setImageResource(mImageIds[position]);
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // слева направо
                setPositionPrev();
                mImageSwitcher.setImageResource(mImageIds[position]);
            }*/
        }
        catch (Exception e)
        {
            // nothing
            return true;
        }
        return true;
    }

    @Override
    public View makeView()
    {
        ImageView imageView = new ImageView(mainActivity);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new
                ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundColor(0xFF000000);
        return imageView;
    }
}
