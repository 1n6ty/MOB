package com.example.mobv2.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.ui.abstractions.HasToolbar;

import java.util.List;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding>
        implements HasToolbar
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private Toolbar toolbar;

    private List<Image> images;
    private ImageView imageView;

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
        imageView = binding.imageView;
        Image startImage = images.get(0);

        if (startImage.getType() == Image.IMAGE_ONLINE)
        {
//            MainActivity.loadImageInView((String) startImage.getPath(), getView(), imageSwitcherView);
        }
        else if (startImage.getType() == Image.IMAGE_OFFLINE)
        {
            imageView.setImageURI((Uri) startImage.getPath());
        }

    }

    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, images.get(0)
                                         .getName());
    }
}
