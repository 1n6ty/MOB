package com.example.mobv2.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.ui.activities.MainActivity;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding>
{
    private Toolbar toolbar;

    private Image image;

    public ImageViewerFragment(Image image)
    {
        super(R.layout.fragment_image_viewer);
        this.image = image;
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
        if (image.getType() == Image.IMAGE_ONLINE)
        {
            MainActivity.loadImageInView((String) image.getPath(), getView(), binding.imageView);
        }
        else if (image.getType() == Image.IMAGE_OFFLINE)
        {
            binding.imageView.setImageURI((Uri) image.getPath());
        }
    }

    @Override
    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, image.getName());
    }
}
