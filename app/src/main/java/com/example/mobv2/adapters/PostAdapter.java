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
import com.example.mobv2.models.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>
{
    private List<Post> posts;

    public PostAdapter(List<Post> posts)
    {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType)
    {
        View postItem = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,
                                 int position)
    {
        Post post = posts.get(position);

        if (post.getAvatar() != null) holder.avatar.setImageBitmap(post.getAvatar());
        holder.fullname.setText(post.getUser()
                                    .toString());
        holder.date.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

        holder.menu.setOnClickListener(
                v ->
                {
// inflate menu
                });
//        holder.reactions.setLayoutManager(new LinearLayoutManager(c));
        holder.reactions.setAdapter(new ReactionAdapter(post.getReactions()));

    }

    @Override
    public int getItemCount()
    {
        return posts.size();
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