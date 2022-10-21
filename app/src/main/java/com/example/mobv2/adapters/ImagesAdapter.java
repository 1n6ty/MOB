package com.example.mobv2.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemImageBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.ImageViewerFragment;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>
{
    private final MainActivity mainActivity;
    private final List<Image> images;

    public ImagesAdapter(MainActivity mainActivity,
                         List<Image> images)
    {
        this.mainActivity = mainActivity;
        this.images = images;
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
        Image image = images.get(position);

        holder.itemView.setOnClickListener(view ->
                mainActivity.goToFragment(new ImageViewerFragment(images), android.R.animator.fade_in));

        if (image.getType() == Image.IMAGE_ONLINE)
        {
//            MainActivity.loadImageInView((String) image.getPath(), holder.itemView, holder.postImageView);
        }
        else if (image.getType() == Image.IMAGE_OFFLINE)
        {
            holder.postImageView.setImageURI((Uri) image.getPath());

        }
    }

    @Override
    public int getItemCount()
    {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView postImageView;

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemImageBinding binding = ItemImageBinding.bind(itemView);

            postImageView = binding.postImageView;
        }
    }
}
