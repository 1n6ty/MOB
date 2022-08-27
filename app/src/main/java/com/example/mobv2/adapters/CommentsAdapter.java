package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.Comment;
import com.example.mobv2.models.Post;
import com.example.mobv2.models.User;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
{
    private final MainActivity mainActivity;
    private final Post post;
    private final List<Comment> comments;

    public CommentsAdapter(MainActivity mainActivity,
                           Post post,
                           List<Comment> comments)
    {
        this.mainActivity = mainActivity;
        this.post = post;
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

        MainActivity.loadImageInView(user.getAvatarUrl(), holder.itemView, holder.avatarView);

        holder.fullnameView.setText(user.getFullname());

        holder.commentTextView.setText(comment.getText());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(comment.getDate()));

        holder.showReactionsView.setOnClickListener(view ->
        {
            holder.reactionsView.setVisibility(holder.reactionsView.getVisibility() == View.GONE
                    ? View.VISIBLE
                    : View.GONE);
        });

        var reactionsAdapter =
                new ReactionsAdapter(mainActivity, comment.getReactions(), comment.getPostId(), comment.getId());
        holder.reactionsView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsView.setAdapter(reactionsAdapter);
    }

    public void addComment(Comment comment,
                           boolean withSort)
    {
        if (!withSort)
        {
            comments.add(comment);
            notifyItemInserted(comments.size() - 1);
            post.getCommentsCount().set(comments.size());
            return;
        }

        Date date = comment.getDate();

        if (comments.isEmpty() || date.compareTo(comments.get(comments.size() - 1)
                                                         .getDate()) < 0)
        {
            comments.add(comment);
            notifyItemInserted(comments.size() - 1);
        }
        else
        {
            for (int i = 0; i < comments.size(); i++)
            {
                Date currentDate = comments.get(i)
                                           .getDate();
                if (date.compareTo(currentDate) >= 0)
                {
                    comments.add(i, comment);
                    notifyItemInserted(i);
                    break;
                }

            }
        }

        post.getCommentsCount().set(comments.size());
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
