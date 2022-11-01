package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.AbleToAdd;
import com.example.mobv2.adapters.abstractions.AbleToReverse;
import com.example.mobv2.adapters.abstractions.AbleToSortByUserWills;
import com.example.mobv2.adapters.abstractions.ForPostsAndCommentsAdapters;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.callbacks.abstractions.MapAdapterCallback;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.PostImpl;
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
import java.util.Locale;

import localdatabase.daos.PostDao;
import localdatabase.daos.UserDao;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
        implements AbleToReverse, AbleToSortByUserWills, AbleToAdd<PostImpl>
{
    private final PostDao postDao;
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private MapAdapterCallback callback;

    private final List<PostImpl> posts;

    public PostsAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        postDao = mainActivity.appDatabase.postDao();
        userDao = mainActivity.appDatabase.userDao();

        posts = new ArrayList<>();
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
        View parentView = holder.itemView;

        var post = posts.get(position);
        var user = post.getUser();
        String postId = post.getId();
        String userId = userDao.getCurrentId();

        holder.setCommentsCount(post.getCommentsCount());
        holder.setAppreciationsCount(post.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, holder.avatarView);

        holder.fullNameView.setText(user.getFullName());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(post.getDate()));

        parentView.setOnClickListener(view -> onItemViewClick(view, holder.getAdapterPosition()));

        if (post.getPositiveRates()
                .contains(userId))
        {
            holder.rateUpButton.setSelected(true);
        }
        else if (post.getNegativeRates()
                     .contains(userId))
        {
            holder.rateDownButton.setSelected(true);
        }

        holder.rateUpButton.setOnClickListener(view -> onRateUpButtonClick(view, parentView, holder.getAdapterPosition()));
        holder.rateDownButton.setOnClickListener(view -> onRateDownButtonClick(view, parentView, holder.getAdapterPosition()));

        holder.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(holder.reactionsRecyclerView));
        holder.showReactionsView.setOnLongClickListener(view -> ForPostsAndCommentsAdapters.onShowReactionsViewLongClick(mainActivity, view));

        ForPostsAndCommentsAdapters.initPostContent(mainActivity, holder.content, post);

        var reactionsAdapter = new ReactionsPostAdapter(mainActivity, post.getReactions(), postId);
        holder.reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsRecyclerView.setAdapter(reactionsAdapter);

        holder.showCommentsView.setOnClickListener(view -> onCommentViewClick(view, holder.getAdapterPosition()));
    }

    private void onItemViewClick(View view,
                                 int position)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_post);

        initMenu(popupMenu, position);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu,
                          int position)
    {
        var menu = popupMenu.getMenu();
        var post = posts.get(position);
        var user = post.getUser();

        boolean isCreator =
                user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
        menu.findItem(R.id.menu_edit_post)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete_post)
            .setVisible(isCreator);

        menu.findItem(R.id.menu_copy_post)
            .setVisible(post.getType() == PostImpl.POST_ONLY_TEXT || post.getType() == PostImpl.POST_FULL);

        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, position));
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
            default:
                return false;
        }
    }

    private void onRateUpButtonClick(View view,
                                     View rootView,
                                     int position)
    {
        onRateButtonClick(view, rootView);
        var post = posts.get(position);
        removeRateFromFirstRatesAndAddRateToSecondRates(post.getNegativeRates(), post.getPositiveRates());

        mainActivity.mobServerAPI.postInc(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);
    }

    private void onRateDownButtonClick(View view,
                                       View rootView,
                                       int position)
    {
        onRateButtonClick(view, rootView);
        var post = posts.get(position);
        removeRateFromFirstRatesAndAddRateToSecondRates(post.getPositiveRates(), post.getNegativeRates());

        mainActivity.mobServerAPI.postDec(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);
    }

    private void onRateButtonClick(View view,
                                   View rootView)
    {
        var rateButton = (ImageButton) view;

        if (rateButton.isSelected())
        {
            rateButton.setSelected(false);
        }
        else
        {
            deselectRateButtons(rootView);
            rateButton.setSelected(true);
        }
    }

    private void deselectRateButtons(View rootView)
    {
        rootView.findViewById(R.id.rate_up_button)
                .setSelected(false);
        rootView.findViewById(R.id.rate_down_button)
                .setSelected(false);
    }

    private void removeRateFromFirstRatesAndAddRateToSecondRates(List<String> firstRates,
                                                                 List<String> secondRates)
    {
        String userId = userDao.getCurrentId();
        firstRates.remove(userId);
        if (!secondRates.remove(userId)) secondRates.add(userId);
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void onCommentViewClick(View view,
                                    int position)
    {
        var post = posts.get(position);

        var reactionsAdapter = new ReactionsPostAdapter(mainActivity, post.getReactions(), post.getId());
        var postItem = new PostItem(post, reactionsAdapter);
        var viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
        viewModel.setPostItem(postItem);
        mainActivity.goToFragment(new CommentsFragment());
    }

    @Override
    public void addElement(@NonNull PostImpl post)
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

    @Override
    public boolean reverse()
    {
        Collections.reverse(posts);
        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    @Override
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

    @Override
    public boolean sortByDate()
    {
        Collections.sort(posts, (post, nextPost) -> (nextPost.getDate()
                                                             .compareTo(post.getDate())));
        notifyItemRangeChanged(0, posts.size());
        return true;
    }

    @Override
    public boolean sortByComments()
    {
        Collections.sort(posts, (post, nextPost) -> Integer.compare(nextPost.getCommentsIds()
                                                                            .size(), post.getCommentsIds()
                                                                                         .size()));
        notifyItemRangeChanged(0, posts.size());
        return true;
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
        PostImpl post = posts.get(position);
        if (callback != null) callback.removeMarkerByPostId(post.getId());
        posts.remove(post);
        notifyItemRemoved(position);

        mainActivity.mobServerAPI.postDelete(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);

        Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
             .show();

        return true;

    }

    public void setMapAdapterCallback(MapAdapterCallback callback)
    {
        this.callback = callback;
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
        private final TextView fullNameView;
        private final TextView dateView;
        private final Pair<TextView, RecyclerView> content;
        private final ImageButton rateUpButton;
        private final TextView ratesCountView;
        private final ImageButton rateDownButton;
        private final View showReactionsView;
        private final RecyclerView reactionsRecyclerView;
        private final LinearLayout showCommentsView;
        private final TextView commentsCountView;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemPostBinding.bind(itemView);

            avatarView = binding.avatarView;
            fullNameView = binding.fullNameView;
            dateView = binding.dateView;
            content = new Pair<>(binding.postTextView, binding.postImagesRecyclerView);
            rateUpButton = binding.rateUpButton;
            ratesCountView = binding.ratesCountView;
            rateDownButton = binding.rateDownButton;
            showReactionsView = binding.showReactionsView;
            reactionsRecyclerView = binding.reactionsRecyclerView;
            showCommentsView = binding.showCommentsView;
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
        private final PostImpl post;
        private final ReactionsPostAdapter reactionsPostAdapter;

        public PostItem(PostImpl post,
                        ReactionsPostAdapter reactionsPostAdapter)
        {
            this.post = post;
            this.reactionsPostAdapter = reactionsPostAdapter;
        }

        public PostImpl getPost()
        {
            return post;
        }

        public ReactionsPostAdapter getReactionsAdapter()
        {
            return reactionsPostAdapter;
        }
    }
}
