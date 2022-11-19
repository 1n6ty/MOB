package com.example.mobv2.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.ui.abstraction.HavingToolbar;

import java.util.List;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding>
        implements HavingToolbar
{
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

    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, images.get(0)
                                         .getName());
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

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_VISIBLE, getResources().getColor(R.color.black));
    }
}