package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.Comment;
import com.example.mobv2.models.Post;
import com.example.mobv2.models.User;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
{
    private final MainActivity mainActivity;
    private final Post post;

    private final List<Comment> comments;

    public CommentsAdapter(MainActivity mainActivity,
                           Post post)
    {
        this.mainActivity = mainActivity;
        this.post = post;
        this.comments = new ArrayList<>();

        var commentsIds = post.getCommentsIds();

        for (int i = 0; i < 4; i++)
        {
            MainActivity.MOB_SERVER_API.getComment(new MOBServerAPI.MOBAPICallback()
            {
                @Override
                public void funcOk(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());

                    var response = (LinkedTreeMap<String, Object>) obj.get("response");

                    Comment comment = new Comment.CommentBuilder().parseFromMap(response);

                    addComment(comment);
                }

                @Override
                public void funcBad(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());
                }

                @Override
                public void fail(Throwable obj)
                {
                    Log.v("DEBUG", obj.toString());
                }
            }, commentsIds.get(commentsIds.size() - 1 - i), MainActivity.token);
        }
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

        holder.fullNameView.setText(user.getFullname());

        holder.commentTextView.setText(comment.getText());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(comment.getDate()));

        holder.itemView.setOnClickListener(view -> onItemViewClick(view, holder.getAdapterPosition()));

        holder.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(holder.reactionsView));

        var reactionsAdapter =
                new ReactionsCommentAdapter(mainActivity, comment.getReactions(), comment.getId());
        holder.reactionsView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsView.setAdapter(reactionsAdapter);
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void addComment(Comment comment)
    {
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
    }

    private void onItemViewClick(View view,
                                 int position)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_post);

        initMenu(view, popupMenu, position);
        popupMenu.show();
    }

    private void initMenu(View view,
                          @NonNull PopupMenu popupMenu,
                          int position)
    {
        var menu = popupMenu.getMenu();
        var comment = comments.get(position);
        var user = comment.getUser();
        var userId = mainActivity.getPrivatePreferences()
                                 .getString(MainActivity.USER_ID_KEY, "");

        boolean isCreator = user.getId()
                                .equals(userId);// if the user is a post's creator
        menu.findItem(R.id.menu_edit_post)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete_post)
            .setVisible(isCreator);

        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, position));

        int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var reactionsView = (RecyclerView) view.findViewById(R.id.reactions_view);
        var reactionsAdapter = (ReactionsCommentAdapter) reactionsView.getAdapter();
        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item -> reactionsAdapter.addReaction(item.getTitle()
                                                                                     .toString()));
        }
    }

    private boolean onMenuItemClick(MenuItem item,
                                    int position)
    {
        switch (item.getItemId())
        {
            case R.id.menu_copy_post:
                return copyComment(position);
            case R.id.menu_forward_post:
                return forwardComment(position);
            case R.id.menu_edit_post:
                return editComment(position);
            case R.id.menu_delete_post:
                return deleteComment(position);
        }

        return false;
    }

    private boolean copyComment(int position)
    {
        var comment = comments.get(position);

        var clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText("simple text", comment.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean forwardComment(int position)
    {
        Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean editComment(int position)
    {
        Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean deleteComment(int position)
    {
        var comment = comments.get(position);

        comments.remove(comment);
        notifyItemRemoved(position);

        post.getCommentsIds()
            .remove(comment.getId());

        MainActivity.MOB_SERVER_API.commentDelete(new MOBAPICallbackImpl(), String.valueOf(comment.getId()), MainActivity.token);

        Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
             .show();

        return true;
    }

    @Override
    public int getItemCount()
    {
        return comments.size();
    }

    protected static class CommentViewHolder extends RecyclerView.ViewHolder
    {
        private final ShapeableImageView avatarView;
        private final TextView fullNameView;
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
            fullNameView = binding.fullNameView;
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
