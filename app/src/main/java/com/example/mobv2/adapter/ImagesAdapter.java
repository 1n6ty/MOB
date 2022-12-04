package com.example.mobv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemImageBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.item.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>
{
    private final MainActivity mainActivity;
    private final List<ImageItem> imageItemList;

    public ImagesAdapter(MainActivity mainActivity,
                         List<Image> images)
    {
        this.mainActivity = mainActivity;
        this.imageItemList = new ArrayList<>();

        for (Image image : images)
        {
            imageItemList.add(new ImageItem(mainActivity, this, image));
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType)
    {
        View imageItem = LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(imageItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder,
                                 int position)
    {
        var imageItem = imageItemList.get(position);

        imageItem.refreshItemBinding(holder.binding);
        imageItem.imageItemHelper.setImageItemList(imageItemList);
    }

    @Override
    public int getItemCount()
    {
        return imageItemList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemImageBinding binding;

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemImageBinding.bind(itemView);
        }
    }
}
