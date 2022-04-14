package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemPostBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>
{


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View postItem =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    protected class PostViewHolder extends RecyclerView.ViewHolder
    {
        private ShapeableImageView avatar;
        private TextView fullname;
        private TextView date;
        private ImageView menu;
        private RecyclerView reactions;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ItemPostBinding binding = ItemPostBinding.bind(itemView);
            avatar = binding.avatarPost;
            fullname = binding.fullnameField;
            date = binding.dateField;
            menu = binding.menu;
            reactions = binding.reactions;
        }
    }
}
