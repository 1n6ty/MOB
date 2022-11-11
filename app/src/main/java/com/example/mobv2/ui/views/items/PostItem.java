package com.example.mobv2.ui.views.items;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapters.ImagesAdapter;
import com.example.mobv2.adapters.MarkersAdapter;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.adapters.ReactionsPostAdapter;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.models.abstractions.HavingCommentsIds;
import com.example.mobv2.ui.abstractions.Item;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.comments.CommentsFragment;
import com.example.mobv2.ui.fragments.comments.CommentsFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import localdatabase.daos.PostDao;
import localdatabase.daos.UserDao;

public class PostItem implements Item<ItemPostBinding>
{
    private final PostDao postDao;
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private final MarkersAdapter markersAdapter;
    private final PostsAdapter postsAdapter;
    private final ReactionsPostAdapter reactionsAdapter;

    public final PostItemHelper postItemHelper;

    private ItemPostBinding postBinding;
    private Menu menu;

    public PostItem(MainActivity mainActivity,
                    MarkersAdapter markersAdapter,
                    PostsAdapter postsAdapter,
                    PostImpl post)
    {
        this.mainActivity = mainActivity;
        this.markersAdapter = markersAdapter;
        this.postsAdapter = postsAdapter;
        this.postItemHelper = new PostItemHelper(post);
        reactionsAdapter =
                new ReactionsPostAdapter(mainActivity, postItemHelper.getReactions(), postItemHelper.getId());

        postDao = mainActivity.appDatabase.postDao();
        userDao = mainActivity.appDatabase.userDao();
    }

    public void refreshItemBinding(@NonNull ItemPostBinding postBinding)
    {
        this.postBinding = postBinding;
        var parentView = postBinding.getRoot();

        var user = postItemHelper.getUser();
        var userId = user.getId();

        parentView.setOnClickListener(this::onPostViewClick);

        postBinding.setCommentsCount(postItemHelper.getCommentsCount());
        postBinding.setRatesCount(postItemHelper.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, postBinding.avatarView);

        postBinding.fullNameView.setText(user.getFullName());

        postBinding.dateView.setText(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(postItemHelper.getDate()));

        if (postItemHelper.getPositiveRates()
                          .contains(userId))
        {
            postBinding.ratesGroup.getRateUpButton()
                                  .setSelected(true);
        }
        else if (postItemHelper.getNegativeRates()
                               .contains(userId))
        {
            postBinding.ratesGroup.getRateDownButton()
                                  .setSelected(true);
        }

        postBinding.ratesGroup.setOnRateUpClickListener(this::onRateUpButtonClick);
        postBinding.ratesGroup.setOnRateDownClickListener(this::onRateDownButtonClick);

        postBinding.showReactionsView.setOnClickListener(this::onShowReactionsViewClick);
        postBinding.showReactionsView.setOnLongClickListener(this::onShowReactionsViewLongClick);

        initContent();

        postBinding.reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        postBinding.reactionsRecyclerView.setAdapter(reactionsAdapter);

        postBinding.showCommentsView.setOnClickListener(this::onCommentViewClick);
    }

    private void onPostViewClick(View view)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_user_item);

        initMenu(popupMenu);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        menu = popupMenu.getMenu();
        var user = postItemHelper.getUser();
        var postType = postItemHelper.getType();

        boolean isCreator = user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
        switchMenuItemVisibility(R.id.menu_edit, isCreator);
        switchMenuItemVisibility(R.id.menu_delete, isCreator);

        switchMenuItemVisibility(R.id.menu_copy_text, postType == PostImpl.POST_ONLY_TEXT || postType == PostImpl.POST_FULL);

        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_copy_text:
                return postItemHelper.copyText();
            case R.id.menu_forward:
                return postItemHelper.forward();
            case R.id.menu_goto_post:
                return postItemHelper.goTo();
            case R.id.menu_edit:
                return postItemHelper.edit();
            case R.id.menu_delete:
                return postItemHelper.delete();
            default:
                return false;
        }
    }

    private void onRateUpButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(postItemHelper.getNegativeRates(), postItemHelper.getPositiveRates());

        mainActivity.mobServerAPI.postInc(new MOBAPICallbackImpl(), postItemHelper.getId(), MainActivity.token);
    }

    private void onRateDownButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(postItemHelper.getPositiveRates(), postItemHelper.getNegativeRates());

        mainActivity.mobServerAPI.postDec(new MOBAPICallbackImpl(), postItemHelper.getId(), MainActivity.token);
    }

    private void removeRateFromFirstRatesAndAddRateToSecondRates(List<String> firstRates,
                                                                 List<String> secondRates)
    {
        var userId = userDao.getCurrentId();
        firstRates.remove(userId);
        if (!secondRates.remove(userId)) secondRates.add(userId);
    }

    private void onShowReactionsViewClick(View view)
    {
        var reactionsRecyclerView = postBinding.reactionsRecyclerView;
        reactionsRecyclerView.setVisibility(reactionsRecyclerView.getVisibility() == View.GONE
                ? View.VISIBLE
                : View.GONE);
    }

    private boolean onShowReactionsViewLongClick(View view)
    {
        final int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_reactions);

        popupMenu.show();

        var menu = popupMenu.getMenu();

        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item ->
                {
                    String emojiItem = item.getTitle()
                                           .toString();
               /* for (Reaction reaction : getReactions())
                {
                    String emoji = reaction.getEmoji();
                    if (emoji.equals(emojiItem))
                    {

                    }
                }*/
                    reactionsAdapter.addElement(emojiItem);
                    return true;
                });
        }

        return true;
    }

    private void initContent()
    {
        postBinding.postTextView.setText(postItemHelper.getText());

        var postImagesRecyclerView = postBinding.postImagesRecyclerView;
        switch (postItemHelper.getType())
        {
            case PostImpl.POST_ONLY_TEXT:
                postImagesRecyclerView.setVisibility(View.GONE);
                break;
            case PostImpl.POST_ONLY_IMAGES:
                postBinding.postTextView.setVisibility(View.GONE);
            case PostImpl.POST_FULL:
                List<Image> images = new ArrayList<>();
                for (String url : postItemHelper.getImages())
                {
                    images.add(new Image("", url, Image.IMAGE_ONLINE));
                }
                ImagesAdapter adapter = new ImagesAdapter(mainActivity, images);
                postImagesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(Math.min(images.size(), 3), StaggeredGridLayoutManager.VERTICAL));
                postImagesRecyclerView.setAdapter(adapter);
                break;
        }
    }

    private void onCommentViewClick(View view)
    {
        var viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
        viewModel.setPostItem(this);
        mainActivity.goToFragment(new CommentsFragment());
    }

    public class PostItemHelper implements HavingCommentsIds
    {
        private final PostImpl post;

        private MarkerInfoItem.MarkerInfoItemHelper markerInfoItemHelper;

        public PostItemHelper(PostImpl post)
        {
            this.post = post;
        }

        public boolean copyText()
        {
            var clipboard =
                    (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            var clip = ClipData.newPlainText("simple text", post.getText());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
                 .show();
            return true;
        }

        public boolean forward()
        {
            Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG)
                 .show();
            return true;
        }

        public boolean goTo()
        {
            if (markerInfoItemHelper != null) markerInfoItemHelper.goTo();

            return true;
        }

        public boolean edit()
        {
            Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
                 .show();
            return true;
        }

        public boolean delete()
        {
            if (markerInfoItemHelper != null) markerInfoItemHelper.delete();
            postsAdapter.deletePostItem(PostItem.this);

            postDao.delete(post);
            mainActivity.mobServerAPI.postDelete(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);

            Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
                 .show();

            return true;
        }

        public void setMarkerInfoItemHelper(MarkerInfoItem.MarkerInfoItemHelper markerInfoItemHelper)
        {
            this.markerInfoItemHelper = markerInfoItemHelper;
        }

        public String getId()
        {
            return post.getId();
        }

        public UserImpl getUser()
        {
            return post.getUser();
        }

        public Date getDate()
        {
            return post.getDate();
        }

        public String getTitle()
        {
            return post.getTitle();
        }

        public String getText()
        {
            return post.getText();
        }

        public List<String> getImages()
        {
            return post.getImages();
        }

        public List<Reaction> getReactions()
        {
            return post.getReactions();
        }

        public List<String> getCommentIds()
        {
            return post.getCommentIds();
        }

        public List<String> getPositiveRates()
        {
            return post.getPositiveRates();
        }

        public List<String> getNegativeRates()
        {
            return post.getNegativeRates();
        }

        public ObservableInt getCommentsCount()
        {
            return post.getCommentsCount();
        }

        public ObservableInt getRatesCount()
        {
            return post.getRatesCount();
        }

        public int getType()
        {
            return post.getType();
        }
    }

    public View getShowReactionsView()
    {
        return postBinding.showReactionsView;
    }

    public View getShowCommentsView()
    {
        return postBinding.showCommentsView;
    }

    public void hideCopyMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_copy_text, false);
    }

    public void showCopyMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_copy_text, true);
    }

    public void hideForwardMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_forward, false);
    }

    public void showForwardMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_forward, true);
    }

    public void hideEditMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_edit, false);
    }

    public void showEditMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_edit, true);
    }

    public void hideDeleteMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_delete, false);
    }

    public void showDeleteMenuItem()
    {
        switchMenuItemVisibility(R.id.menu_delete, true);
    }

    private void switchMenuItemVisibility(int menuItemId,
                                          boolean visible)
    {
        if (menu != null) menu.findItem(menuItemId)
                              .setVisible(visible);
    }
}
