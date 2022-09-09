package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.models.Post;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragment;
import com.example.mobv2.ui.fragments.comments.CommentsFragmentViewModel;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
{
    private final MainActivity mainActivity;
    private final List<Post> posts;
    private final MapAdapter mapAdapter;

    // TODO destroy the mapAdapter from this side
    public PostsAdapter(MainActivity mainActivity,
                        List<Post> posts,
                        MapAdapter mapAdapter)
    {
        this.mainActivity = mainActivity;
        this.posts = posts;
        this.mapAdapter = mapAdapter;
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

        MainActivity.loadImageInView(user.getAvatarUrl(), holder.itemView, holder.avatarView);

        holder.fullnameView.setText(user.getFullname());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

        holder.itemView.setOnClickListener(view -> onItemViewClick(view, holder.getAdapterPosition()));

        holder.appreciationUpView.setOnClickListener(view -> onAppreciationClick((RadioButton) view, holder.getAdapterPosition(), true));
        holder.setAppreciationsCount(post.getAppreciationsCount());
        holder.appreciationDownView.setOnClickListener(view -> onAppreciationClick((RadioButton) view, holder.getAdapterPosition(), false));
//        holder.appreciationUpView.getDrawable().setTint(0);
//        holder.appreciationDownView.getDrawable().setTint(0);
        if (post.getAppreciated() == 1)
        {
            holder.appreciationUpView.setChecked(true);
        }
        else if (post.getAppreciated() == 0)
        {
            holder.appreciationDownView.setChecked(true);
        }

        holder.showReactionsView.setOnClickListener((view) -> onShowReactionsViewClick(holder.reactionsView));

        initContent(holder.content, position);

        var reactionsAdapter =
                new ReactionsAdapter(mainActivity, post.getReactions(), post.getId());
        holder.reactionsView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsView.setAdapter(reactionsAdapter);

        holder.commentView.setOnClickListener(view ->
        {
            var postItem = new PostItem(post, reactionsAdapter, this::onShowReactionsViewClick);
            var viewModel =
                    new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
            viewModel.setPostItem(postItem);
            viewModel.setCommentsCount(post.getCommentsCount()
                                           .get());
            mainActivity.goToFragment(new CommentsFragment());
        });

        holder.setCommentsCount(post.getCommentsCount());
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE
                ? View.VISIBLE
                : View.GONE);
    }

    private void onAppreciationClick(RadioButton appreciationButton,
                                     int position,
                                     boolean isPositive)
    {
        Post post = posts.get(position);
        ObservableInt appreciationsCount = post.getAppreciationsCount();

        if (post.getAppreciated() == 1)
        {
            MainActivity.MOB_SERVER_API.postInc(new MOBServerAPI.MOBAPICallback()
            {
                @Override
                public void funcOk(LinkedTreeMap<String, Object> obj)
                {
                    post.setAppreciated(-1);
                    appreciationsCount.set(appreciationsCount.get() - 1);
                    appreciationButton.setChecked(false);
                }

                @Override
                public void funcBad(LinkedTreeMap<String, Object> obj)
                {

                }

                @Override
                public void fail(Throwable obj)
                {

                }
            }, post.getId(), MainActivity.token);
        }
        else if (post.getAppreciated() == 0)
        {
            MainActivity.MOB_SERVER_API.postDec(new MOBServerAPI.MOBAPICallback()
            {
                @Override
                public void funcOk(LinkedTreeMap<String, Object> obj)
                {
                    post.setAppreciated(-1);
                    appreciationsCount.set(appreciationsCount.get() + 1);
                    appreciationButton.setChecked(false);
                }

                @Override
                public void funcBad(LinkedTreeMap<String, Object> obj)
                {

                }

                @Override
                public void fail(Throwable obj)
                {

                }
            }, post.getId(), MainActivity.token);
        }
        else
        {
            if (isPositive)
            {
                MainActivity.MOB_SERVER_API.postInc(new MOBServerAPI.MOBAPICallback()
                {
                    @Override
                    public void funcOk(LinkedTreeMap<String, Object> obj)
                    {
                        post.setAppreciated(1);
                        appreciationsCount.set(appreciationsCount.get() + 1);
                        appreciationButton.setChecked(true);
                    }

                    @Override
                    public void funcBad(LinkedTreeMap<String, Object> obj)
                    {

                    }

                    @Override
                    public void fail(Throwable obj)
                    {

                    }
                }, post.getId(), MainActivity.token);
            }
            else
            {
                MainActivity.MOB_SERVER_API.postDec(new MOBServerAPI.MOBAPICallback()
                {
                    @Override
                    public void funcOk(LinkedTreeMap<String, Object> obj)
                    {
                        post.setAppreciated(0);
                        appreciationsCount.set(appreciationsCount.get() - 1);
                        appreciationButton.setChecked(true);
                    }

                    @Override
                    public void funcBad(LinkedTreeMap<String, Object> obj)
                    {

                    }

                    @Override
                    public void fail(Throwable obj)
                    {

                    }
                }, post.getId(), MainActivity.token);
            }
        }
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
        Collections.sort(posts, (o1, o2) -> Integer.compare(o2.getAppreciationsCount()
                                                              .get(), o1.getAppreciationsCount()
                                                                        .get()));

        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    public boolean sortByDate()
    {
        Collections.sort(posts, (o1, o2) -> (o2.getDate()
                                               .compareTo(o1.getDate())));

        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    public boolean sortByComments()
    {
        Collections.sort(posts, (o1, o2) -> Integer.compare(o2.getCommentsCount()
                                                              .get(), o1.getCommentsCount()
                                                                        .get()));

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
                    return copyPost(position);
                case R.id.menu_forward_post:
                    return forwardPost(position);
                case R.id.menu_edit_post:
                    return editPost(position);
                case R.id.menu_delete_post:
                    return deletePost(position);
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

    private void initContent(@NonNull Pair<TextView, RecyclerView> content,
                             int position)
    {
        Post post = posts.get(position);

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
        Post post = posts.get(position);

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

        MainActivity.MOB_SERVER_API.postDelete(new MOBServerAPI.MOBAPICallback()
        {
            @Override
            public void funcOk(LinkedTreeMap<String, Object> obj)
            {
                Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
                     .show();
                mapAdapter.removeMarkerByPostId(post.getId());
                posts.remove(post);
                notifyItemRemoved(position);
            }

            @Override
            public void funcBad(LinkedTreeMap<String, Object> obj)
            {

            }

            @Override
            public void fail(Throwable obj)
            {

            }
        }, post.getId(), MainActivity.token);
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

        private void setCommentsCount(ObservableInt count)
        {
            binding.setCommentsCount(count);
        }

        private void setAppreciationsCount(ObservableInt count)
        {
            binding.setAppreciationsCount(count);
        }
    }

    public static class PostItem
    {
        private final Post post;
        private final ReactionsAdapter reactionsAdapter;

        private final View.OnClickListener onShowReactionsViewClickListener;

        public PostItem(Post post,
                        ReactionsAdapter reactionsAdapter,
                        View.OnClickListener onShowReactionsViewClickListener)
        {
            this.post = post;
            this.reactionsAdapter = reactionsAdapter;
            this.onShowReactionsViewClickListener = onShowReactionsViewClickListener;
        }

        public Post getPost()
        {
            return post;
        }

        public ReactionsAdapter getReactionsAdapter()
        {
            return reactionsAdapter;
        }

        public void onShowReactionsViewClick(View view)
        {
            onShowReactionsViewClickListener.onClick(view);
        }
    }
}