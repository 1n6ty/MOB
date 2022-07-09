package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.Post;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragment;
import com.example.mobv2.utils.abstractions.Action;
import com.google.android.material.imageview.ShapeableImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
{
    private final MainActivity mainActivity;
    private final List<Post> posts;
    private final Action<String> titleMarkerAction;

    public PostsAdapter(MainActivity mainActivity,
                        List<Post> posts,
                        Action<String> titleMarkerAction)
    {
        this.mainActivity = mainActivity;
        this.posts = posts;
        this.titleMarkerAction = titleMarkerAction;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType)
    {
        View postItem = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postItem);
    }


    @Override
    public void onViewAttachedToWindow(@NonNull PostViewHolder holder)
    {
        var position = holder.getAdapterPosition() - 1;
//        if (position == posts.size() - 2)
//        {
//            titleMarkerAction.execute(post.getTitle());
//        }
        if (position > -1)
        {
            var post = posts.get(position);
            titleMarkerAction.execute(post.getTitle());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,
                                 int position)
    {
        var post = posts.get(position);
        var user = post.getUser();

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

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

        holder.itemView.setOnClickListener(view -> onItemViewClicked(view, position));

        holder.showReactionsView.setOnClickListener(view ->
        {
            holder.reactionsView.setVisibility(holder.reactionsView.getVisibility() == View.GONE
                    ? View.VISIBLE
                    : View.GONE);
        });

        initContent(holder.content, position);

        var reactionsAdapter =
                new ReactionsAdapter(mainActivity, post.getReactions(), post.getId());
        holder.reactionsView.setAdapter(reactionsAdapter);

        holder.commentView.setOnClickListener(view -> mainActivity.transactionToFragment(new CommentsFragment(post)));
    }

    public void addPost(Post post)
    {
        posts.add(post);
        notifyItemInserted(posts.size() - 1);
    }

    private void onItemViewClicked(View view,
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
        var post = posts.get(position);
        var user = post.getUser();
        var userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);
        var menu = popupMenu.getMenu();

        if (user.getId() == userId) // if the user is a post's creator
        {
            menu.findItem(R.id.menu_edit_post)
                .setVisible(true);
            menu.findItem(R.id.menu_delete_post)
                .setVisible(true);
        }

        switch (post.getType())
        {
            case Post.POST_ONLY_TEXT:
            case Post.POST_FULL:
                menu.findItem(R.id.menu_copy_post)
                    .setVisible(true);
                break;
            case Post.POST_ONLY_IMAGES:
                menu.findItem(R.id.menu_copy_post)
                    .setVisible(false);
                break;
        }

        popupMenu.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.menu_copy_post:
                    return copyPost(post);
                case R.id.menu_forward_post:
                    return forwardPost(post);
                case R.id.menu_edit_post:
                    return editPost(post);
                case R.id.menu_delete_post:
                    deletePost(post);
            }

            return false;
        });

        int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var reactionsView = (RecyclerView) view.findViewById(R.id.reactions_view);
        var reactionsAdapter = (ReactionsAdapter) reactionsView.getAdapter();
        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item -> reactionsAdapter.addReaction(item.getTitle()
                                                                                     .toString()));
        }
    }

    private void initContent(@NonNull Pair<TextView, ImageView> content,
                             int position)
    {
        Post post = posts.get(position);

        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + post.getImages()
                                                            .get(0));
        }
        catch (MalformedURLException e)
        {
            return;
        }

        switch (post.getType())
        {
            case Post.POST_ONLY_TEXT:
                content.first.setText(post.getText());
                content.first.setVisibility(View.VISIBLE);
                content.second.setVisibility(View.GONE);
                break;
            case Post.POST_FULL:
                content.first.setText(post.getText());
                content.first.setVisibility(View.VISIBLE);
                Glide.with(mainActivity)
                     .load(url)
                     .into(content.second);
                content.second.setVisibility(View.VISIBLE);
                break;
            case Post.POST_ONLY_IMAGES:
                content.first.setVisibility(View.GONE);
                Glide.with(mainActivity)
                     .load(url)
                     .into(content.second);
                content.second.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean copyPost(@NonNull Post post)
    {
        var clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText("simple text", post.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean forwardPost(Post post)
    {
        Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean editPost(Post post)
    {
        Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean deletePost(Post post)
    {
        Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    protected static class PostViewHolder extends RecyclerView.ViewHolder
    {
        private final ShapeableImageView avatarView;
        private final TextView fullnameView;
        private final TextView dateView;
        private final Pair<TextView, ImageView> content;
        private final View appreciationUpView;
        private final TextView appreciationsCountView;
        private final View appreciationDownView;
        private final View showReactionsView;
        private final RecyclerView reactionsView;
        private final View commentView;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemPostBinding binding = ItemPostBinding.bind(itemView);
            avatarView = binding.avatarView;
            fullnameView = binding.fullnameView;
            dateView = binding.dateView;
            content = new Pair<>(binding.postTextView, binding.postImageView);
            appreciationUpView = binding.appreciationUpView;
            appreciationsCountView = binding.appreciationsCountView;
            appreciationDownView = binding.appreciationDownView;
            showReactionsView = binding.showReactionsView;
            reactionsView = binding.reactionsView;
            commentView = binding.commentView;
        }
    }
}