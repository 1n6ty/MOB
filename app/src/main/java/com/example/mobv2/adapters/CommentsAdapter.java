package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.Comment;
import com.example.mobv2.models.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
{
    private final List<Comment> comments;

    public CommentsAdapter(List<Comment> comments)
    {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType)
    {
        View commentItem = LayoutInflater.from(parent.getContext())
                                         .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(commentItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder,
                                 int position)
    {
        Comment comment = comments.get(position);
        User user = comment.getUser();

        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + user.getAvatarUrl());
        }
        catch (MalformedURLException e)
        {
            return;
        }

        Glide.with(holder.itemView)
             .load(url)
             .into(holder.avatarView);

        holder.fullnameView.setText(user.getFullname());

        holder.commentTextView.setText(comment.getText());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(comment.getDate()));

        holder.showReactionsView.setOnClickListener(view ->
        {
            holder.reactionsView.setVisibility(holder.reactionsView.getVisibility() == View.GONE
                    ? View.VISIBLE
                    : View.GONE);
        });
    }

    public void addComment(Comment comment)
    {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    @Override
    public int getItemCount()
    {
        return comments.size();
    }

    protected static class CommentViewHolder extends RecyclerView.ViewHolder
    {
        private final ShapeableImageView avatarView;
        private final TextView fullnameView;
        private final TextView commentTextView;
        private final TextView dateView;
        private final ImageView appreciationUpView;
        private final TextView appreciationsCountView;
        private final ImageView appreciationDownView;
        private final ImageView showReactionsView;
        private final RecyclerView reactionsView;

        public CommentViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemCommentBinding binding = ItemCommentBinding.bind(itemView);
            avatarView = binding.avatarView;
            fullnameView = binding.fullnameView;
            commentTextView = binding.commentTextView;
            dateView = binding.commentDateView;
            appreciationUpView = binding.appreciationUpView;
            appreciationsCountView = binding.appreciationsCountView;
            appreciationDownView = binding.appreciationDownView;
            showReactionsView = binding.showReactionsView;
            reactionsView = binding.reactionsView;
        }
    }
}
