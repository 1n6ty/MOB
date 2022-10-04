package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.models.Post;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragment;
import com.example.mobv2.ui.fragments.comments.CommentsFragmentViewModel;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
{
    private final MainActivity mainActivity;
    private final MapAdapter mapAdapter;

    private final List<Post> posts;

    // TODO destroy the mapAdapter from this side
    public PostsAdapter(MainActivity mainActivity,
                        MapAdapter mapAdapter)
    {
        this.mainActivity = mainActivity;
        this.mapAdapter = mapAdapter;
        this.posts = new ArrayList<>();
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
        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);

        var position = holder.getAdapterPosition();

        if (position > -1)
        {
            var post = posts.get(position);

            mainFragmentViewModel.setPostTitle(post.getTitle());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,
                                 int position)
    {
        var post = posts.get(position);
        var user = post.getUser();

        holder.binding.setCommentsCount(post.getCommentsCount());
        holder.binding.setAppreciationsCount(post.getAppreciationsCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), holder.itemView, holder.avatarView);

        holder.fullnameView.setText(user.getFullname());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

        holder.itemView.setOnClickListener(view -> onItemViewClick(view, holder.getAdapterPosition()));

        holder.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(holder.reactionsView));

        initContent(holder.content, position);

        var reactionsAdapter =
                new ReactionsPostAdapter(mainActivity, post.getReactions(), post.getId());
        holder.reactionsView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsView.setAdapter(reactionsAdapter);

        holder.commentView.setOnClickListener(view ->
        {
            var postItem = new PostItem(post, reactionsAdapter);
            var viewModel =
                    new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
            viewModel.setPostItem(postItem);
            mainActivity.goToFragment(new CommentsFragment());
        });
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE
                ? View.VISIBLE
                : View.GONE);
    }

    public void addPost(Post post)
    {
        Date date = post.getDate();
        if (posts.isEmpty() || date.compareTo(posts.get(posts.size() - 1)
                                                   .getDate()) < 0)
        {
            posts.add(post);
            notifyItemInserted(posts.size() - 1);
        }
        else
        {
            for (int i = 0; i < posts.size(); i++)
            {
                Date currentDate = posts.get(i)
                                        .getDate();
                if (date.compareTo(currentDate) >= 0)
                {
                    posts.add(i, post);
                    notifyItemInserted(i);
                    break;
                }
            }
        }
    }

    public boolean reverse()
    {
        Collections.reverse(posts);
        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    public boolean sortByAppreciations()
    {
        Collections.sort(posts, (post, nextPost) ->
        {
            int postPositiveRatesSize = post.getPositiveRates()
                                            .size();
            int nextPostPositiveRatesSize = nextPost.getPositiveRates()
                                                    .size();
            return Integer.compare(nextPostPositiveRatesSize, postPositiveRatesSize);
        });
        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    public boolean sortByDate()
    {
        Collections.sort(posts, (post, nextPost) -> (nextPost.getDate()
                                                             .compareTo(post.getDate())));
        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    public boolean sortByComments()
    {
        Collections.sort(posts, (post, nextPost) -> Integer.compare(nextPost.getCommentsIds()
                                                                            .size(), post.getCommentsIds()
                                                                                         .size()));
        notifyItemRangeChanged(0, posts.size());
        return true;
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
        var post = posts.get(position);
        var user = post.getUser();
        var userId = mainActivity.getPrivatePreferences()
                                 .getString(MainActivity.USER_ID_KEY, "");
        var menu = popupMenu.getMenu();

        boolean isCreator = user.getId()
                                .equals(userId);// if the user is a post's creator
        menu.findItem(R.id.menu_edit_post)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete_post)
            .setVisible(isCreator);

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

        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, position));

        int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var reactionsView = (RecyclerView) view.findViewById(R.id.reactions_view);
        var reactionsAdapter = (ReactionsPostAdapter) reactionsView.getAdapter();
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
                return copyPost(position);
            case R.id.menu_forward_post:
                return forwardPost(position);
            case R.id.menu_edit_post:
                return editPost(position);
            case R.id.menu_delete_post:
                return deletePost(position);
        }

        return false;
    }

    private void initContent(@NonNull Pair<TextView, RecyclerView> content,
                             int position)
    {
        var post = posts.get(position);

        content.first.setText(post.getText());

        switch (post.getType())
        {
            case Post.POST_ONLY_TEXT:
                content.second.setVisibility(View.GONE);
                break;
            case Post.POST_ONLY_IMAGES:
                content.first.setVisibility(View.GONE);
            case Post.POST_FULL:
                List<Image> images = new ArrayList<>();
                for (String url : post.getImages())
                {
                    images.add(new Image("", url, Image.IMAGE_ONLINE));
                }
                ImagesAdapter adapter = new ImagesAdapter(mainActivity, images);
                content.second.setLayoutManager(new StaggeredGridLayoutManager(Math.min(images.size(), 3), StaggeredGridLayoutManager.VERTICAL));
                content.second.setAdapter(adapter);
                break;
        }
    }

    private boolean copyPost(int position)
    {
        var post = posts.get(position);

        var clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText("simple text", post.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean forwardPost(int position)
    {
        Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean editPost(int position)
    {
        Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean deletePost(int position)
    {
        Post post = posts.get(position);

        mapAdapter.removeMarkerByPostId(post.getId());
        posts.remove(post);
        notifyItemRemoved(position);

        MainActivity.MOB_SERVER_API.postDelete(new MOBAPICallbackImpl(), String.valueOf(post.getId()), MainActivity.token);

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
        private final com.example.mobv2.databinding.ItemPostBinding binding;

        private final ShapeableImageView avatarView;
        private final TextView fullnameView;
        private final TextView dateView;
        private final Pair<TextView, RecyclerView> content;
        private final RadioButton appreciationUpView;
        private final TextView appreciationsCountView;
        private final RadioButton appreciationDownView;
        private final View showReactionsView;
        private final RecyclerView reactionsView;
        private final LinearLayout commentView;
        private final TextView commentsCountView;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemPostBinding.bind(itemView);

            avatarView = binding.avatarView;
            fullnameView = binding.fullnameView;
            dateView = binding.dateView;
            content = new Pair<>(binding.postTextView, binding.postImagesView);
            appreciationUpView = binding.appreciationUpButton;
            appreciationsCountView = binding.appreciationsCountView;
            appreciationDownView = binding.appreciationDownButton;
            showReactionsView = binding.showReactionsView;
            reactionsView = binding.reactionsView;
            commentView = binding.commentView;
            commentsCountView = binding.commentsCountView;
        }

//        private void setCommentsCount(ObservableInt count)
//        {
//            binding.setCommentsCount(count);
//        }
//
//        private void setAppreciationsCount(ObservableInt count)
//        {
//            binding.setAppreciationsCount(count);
//        }
    }

    public static class PostItem
    {
        private final Post post;
        private final ReactionsPostAdapter reactionsPostAdapter;

        public PostItem(Post post,
                        ReactionsPostAdapter reactionsPostAdapter)
        {
            this.post = post;
            this.reactionsPostAdapter = reactionsPostAdapter;
        }

        public Post getPost()
        {
            return post;
        }

        public ReactionsPostAdapter getReactionsAdapter()
        {
            return reactionsPostAdapter;
        }
    }
}