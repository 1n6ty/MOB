package com.example.mobv2.ui.view.item;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.adapter.ImagesAdapter;
import com.example.mobv2.databinding.ItemImageBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.ui.abstraction.Item;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.imageViewerFragment.ImageViewerFragment;
import com.example.mobv2.ui.fragment.imageViewerFragment.ImageViewerFragmentViewModel;

import java.util.List;

public class ImageItem implements Item<ItemImageBinding>
{
    private final MainActivity mainActivity;
    private final ImagesAdapter imagesAdapter;

    public final ImageItemHelper imageItemHelper;

    private ItemImageBinding binding;

    public ImageItem(MainActivity mainActivity,
                     ImagesAdapter imagesAdapter,
                     Image image)
    {
        this.mainActivity = mainActivity;
        this.imagesAdapter = imagesAdapter;
        this.imageItemHelper = new ImageItemHelper(image);
    }

    @Override
    public void refreshItemBinding(@NonNull ItemImageBinding binding)
    {
        this.binding = binding;
        var parentView = binding.getRoot();

        parentView.setOnClickListener(view ->
        {
            var viewModel =
                    new ViewModelProvider(mainActivity).get(ImageViewerFragmentViewModel.class);
            viewModel.setImageItemList(imageItemHelper.imageItemList);
            mainActivity.goToFragment(new ImageViewerFragment(), android.R.animator.fade_in);
        });

        var image = imageItemHelper.image;
        if (image.getType() == Image.IMAGE_ONLINE)
        {
            MainActivity.loadImageInView((String) image.getPath(), parentView, binding.postImageView);
        }
        else if (image.getType() == Image.IMAGE_OFFLINE)
        {
            binding.postImageView.setImageURI((Uri) image.getPath());
        }
    }

    public class ImageItemHelper
    {
        private final Image image;

        private List<ImageItem> imageItemList;

        public ImageItemHelper(Image image)
        {
            this.image = image;
        }

        public void setImageItemList(List<ImageItem> imageItemList)
        {
            this.imageItemList = imageItemList;
        }

        public String getName()
        {
            return image.getName();
        }

        public Object getPath()
        {
            return image.getPath();
        }

        public int getType()
        {
            return image.getType();
        }
    }
}
