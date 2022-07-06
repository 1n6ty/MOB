package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.Post;
import com.example.mobv2.utils.abstractions.Operation;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>
{
    private final Context context;
    private final List<Post> posts;

    public PostAdapter(
            Context context,
            List<Post> posts)
    {
        this.context = context;
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

//        holder.avatar.setImageBitmap(post.getAvatar());  not yet

        holder.fullnameView.setText(post.getUser()
                                        .toString());
        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

        holder.menuView.setOnClickListener(v -> onMenuViewClicked(v, position));

        initContent(holder.content, position);

        holder.reactionsView.setAdapter(new ReactionAdapter(context, post.getReactions()));
    }

    public void addPost(Post post)
    {
        posts.add(post);
        notifyItemInserted(posts.size() - 1);
    }

    private void onMenuViewClicked(View v,
                                   int position)
    {
        Context contextThemeWrapper =
                new ContextThemeWrapper(context, R.style.Theme_MOBv2_PopupOverlay);
        PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, v);
        popupMenu.inflate(R.menu.menu_post);

        initMenu(popupMenu, position);
        // inflate menu
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu,
                          int position)
    {
        Post post = posts.get(position);

        Menu menu = popupMenu.getMenu();

        if (true) // if the user is a post's creator
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

        HashMap<Integer, Operation<Post, Boolean>> popupMenuCommands =
                new HashMap<Integer, Operation<Post, Boolean>>()
                {
                    {
                        put(R.id.menu_copy_post, PostAdapter.this::copyPost);
                        put(R.id.menu_forward_post, PostAdapter.this::forwardPost);
                        put(R.id.menu_edit_post, PostAdapter.this::editPost);
                        put(R.id.menu_delete_post, PostAdapter.this::deletePost);
                    }
                };

        for (Integer id : popupMenuCommands.keySet())
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item -> Objects.requireNonNull(popupMenuCommands.get(id))
                                                           .execute(post));
        }
    }

    private void initContent(@NonNull Pair<TextView, ImageView> content,
                             int position)
    {
        Post post = posts.get(position);

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
                content.second.setVisibility(View.VISIBLE);
                break;
            case Post.POST_ONLY_IMAGES:
                content.first.setVisibility(View.GONE);
                content.second.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean copyPost(@NonNull Post post)
    {
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", post.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean forwardPost(Post post)
    {
        Toast.makeText(context, "Forwarded", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean editPost(Post post)
    {
        Toast.makeText(context, "Edited", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean deletePost(Post post)
    {
        Toast.makeText(context, "Deleted", Toast.LENGTH_LONG)
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
        private final ImageView menuView;
        private final Pair<TextView, ImageView> content;
        private final RecyclerView reactionsView;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemPostBinding binding = ItemPostBinding.bind(itemView);
            avatarView = binding.avatarView;
            fullnameView = binding.fullnameView;
            dateView = binding.dateView;
            menuView = binding.menuView;
            content = new Pair<>(binding.postTextView, binding.postImageView);
            reactionsView = binding.reactionsView;
        }
    }
}