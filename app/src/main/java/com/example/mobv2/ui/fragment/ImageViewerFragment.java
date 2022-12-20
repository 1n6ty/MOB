package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentImageViewerBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.item.ImageItem;

import java.util.List;

public class ImageViewerFragment extends BaseFragment<FragmentImageViewerBinding>
        implements HavingToolbar
{
    private List<ImageItem> imageItemList;

    public ImageViewerFragment()
    {
        super(R.layout.fragment_image_viewer);
    }

    public static ImageViewerFragment newInstance()
    {
        return new ImageViewerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        var transitionFade = TransitionInflater.from(mainActivity)
                                               .inflateTransition(android.R.transition.fade);
        setExitTransition(transitionFade);
        setEnterTransition(transitionFade);
        setSharedElementEnterTransition(
                TransitionInflater.from(mainActivity).inflateTransition(R.transition.shared_image));
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
        super.initToolbar(binding.toolbar, imageItemList.get(0).imageItemHelper.getName());
    }

    private void initImageView()
    {
        var startImage = imageItemList.get(0).imageItemHelper;
//
//        if (startImage.getType() == Image.IMAGE_ONLINE)
//        {
//            MainActivity.loadImageInView((String) startImage.getPath(), getView(),
//                    binding.imageView);
//        }
//        else if (startImage.getType() == Image.IMAGE_OFFLINE)
//        {
//            binding.imageView.setImageURI((Uri) startImage.getPath());
//        }
    }

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_VISIBLE, getResources().getColor(R.color.black));
    }

    public void setImageItemList(List<ImageItem> imageItemList)
    {
        this.imageItemList = imageItemList;
    }
}
