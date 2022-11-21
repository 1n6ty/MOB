package com.example.mobv2.ui.fragment.imageViewerFragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding> implements HavingToolbar
{
    private ImageViewerFragmentViewModel viewModel;

    public ImageViewerFragment()
    {
        super(R.layout.fragment_image_viewer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        initViewModel();

        return view;
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(ImageViewerFragmentViewModel.class);
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
        super.initToolbar(binding.toolbar, viewModel.imageItemList.get(0).imageItemHelper.getName());
    }

    private void initImageView()
    {
        var startImage = viewModel.imageItemList.get(0).imageItemHelper;

        if (startImage.getType() == Image.IMAGE_ONLINE)
        {
            MainActivity.loadImageInView((String) startImage.getPath(), getView(), binding.imageView);
        }
        else if (startImage.getType() == Image.IMAGE_OFFLINE)
        {
            binding.imageView.setImageURI((Uri) startImage.getPath());
        }

    }

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_VISIBLE, getResources().getColor(R.color.black));
    }
}
