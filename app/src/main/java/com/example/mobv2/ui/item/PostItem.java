package com.example.mobv2.ui.item;

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
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapter.ImagesAdapter;
import com.example.mobv2.adapter.PostsAdapter;
import com.example.mobv2.adapter.ReactionsAdapter;
import com.example.mobv2.callback.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.model.PostImpl;
import com.example.mobv2.model.Reaction;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.model.abstraction.HavingCommentsIds;
import com.example.mobv2.ui.abstraction.Item;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.fragment.CommentsFragment;
import com.example.mobv2.util.DateString;
import com.example.mobv2.util.MyObservableArrayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostItem implements Item<ItemPostBinding>
{
    public final PostItemHelper postItemHelper;
    private final MainActivity mainActivity;
    private final PostsAdapter postsAdapter;
    private ReactionsAdapter reactionsAdapter;
    private ItemPostBinding binding;

    private Menu menu;

    public PostItem(MainActivity mainActivity,
                    PostsAdapter postsAdapter,
                    PostImpl post)
    {
        this.mainActivity = mainActivity;
        this.postsAdapter = postsAdapter;
        this.postItemHelper = new PostItemHelper(post);
    }

    public void refreshItemBinding(@NonNull ItemPostBinding binding)
    {
        this.binding = binding;
        var parentView = binding.getRoot();

        initInfo();

        parentView.setOnClickListener(this::onPostViewClick);

        initContent();
        initRatesGroup();
        initShowReactionsButton();
        initReactionsRecyclerView();
        initShowCommentsButton();
    }

    private void initInfo()
    {
        var parentView = binding.getRoot();

        var user = postItemHelper.getUser();

        binding.setFullName(user.getFullName());
        binding.setDate(postItemHelper.getDateString());
        binding.setCommentsCount(postItemHelper.getCommentsCount());
        binding.setRatesCount(postItemHelper.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, binding.avatarView);
    }

    private void onPostViewClick(View view)
    {
        var contextThemeWrapper = new ContextThemeWrapper(mainActivity,
                R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_item_post);

        initMenu(popupMenu);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        menu = popupMenu.getMenu();
        var user = postItemHelper.getUser();
        var postType = postItemHelper.getType();

        var currentUser = mainActivity.appDatabase.userDao().getCurrentOne();

        boolean isCreator = user.compareById(currentUser);  // if the user is a post's creator
        switchMenuItemVisibility(R.id.menu_edit, isCreator);
        switchMenuItemVisibility(R.id.menu_delete, isCreator);

        switchMenuItemVisibility(R.id.menu_copy_text,
                postType == PostItemHelper.POST_ONLY_TEXT || postType == PostItemHelper.POST_FULL);

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

    private void initContent()
    {
        binding.setText(postItemHelper.getText());

        var textView = binding.textView;
        var imagesRecyclerView = binding.imagesRecyclerView;
        switch (postItemHelper.getType())
        {
            case PostItemHelper.POST_ONLY_TEXT:
                imagesRecyclerView.setVisibility(View.GONE);
                break;
            case PostItemHelper.POST_ONLY_IMAGES:
                textView.setVisibility(View.GONE);
            case PostItemHelper.POST_FULL:
                List<Image> images = new ArrayList<>();
                for (String url : postItemHelper.getImages())
                {
                    images.add(new Image("", url, Image.IMAGE_ONLINE));
                }
                ImagesAdapter adapter = new ImagesAdapter(mainActivity, images);
                imagesRecyclerView.setLayoutManager(
                        new GridLayoutManager(mainActivity, Math.min(images.size(), 3)));
                imagesRecyclerView.setAdapter(adapter);
                break;
        }
    }

    private void initRatesGroup()
    {
        var ratesGroup = binding.ratesGroup;

        var user = postItemHelper.getUser();
        var userId = user.getId();

        ratesGroup.getRateUpButton().setSelected(false);
        ratesGroup.getRateDownButton().setSelected(false);

        if (postItemHelper.getPositiveRates().contains(userId))
        {
            ratesGroup.getRateUpButton().setSelected(true);
        }
        else if (postItemHelper.getNegativeRates().contains(userId))
        {
            ratesGroup.getRateDownButton().setSelected(true);
        }

        ratesGroup.setOnRateUpClickListener(this::onRateUpButtonClick);
        ratesGroup.setOnRateDownClickListener(this::onRateDownButtonClick);
    }

    private void onRateUpButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(postItemHelper.getNegativeRates(),
                postItemHelper.getPositiveRates());

        mainActivity.mobServerAPI.postInc(new MOBAPICallbackImpl(), postItemHelper.getId(),
                MainActivity.token);
    }

    private void onRateDownButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(postItemHelper.getPositiveRates(),
                postItemHelper.getNegativeRates());

        mainActivity.mobServerAPI.postDec(new MOBAPICallbackImpl(), postItemHelper.getId(),
                MainActivity.token);
    }

    private void removeRateFromFirstRatesAndAddRateToSecondRates(List<String> firstRates,
                                                                 List<String> secondRates)
    {
        var userId = mainActivity.appDatabase.userDao().getCurrentId();
        firstRates.remove(userId);
        if (!secondRates.remove(userId))
        {
            secondRates.add(userId);
        }
    }

    private void initShowReactionsButton()
    {
        var showReactionsButton = binding.showReactionsButton;

        showReactionsButton.setOnClickListener(this::onShowReactionsViewClick);
        showReactionsButton.setOnLongClickListener(this::onShowReactionsViewLongClick);
    }

    private void onShowReactionsViewClick(View view)
    {
        var reactionsRecyclerView = binding.reactionsRecyclerView;

        if (reactionsAdapter == null)
        {
            initAdapterForReactionsRecyclerView();
        }

        reactionsRecyclerView.setVisibility(
                reactionsRecyclerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private boolean onShowReactionsViewLongClick(View view)
    {
        final int[] menuIds = {R.id.menu_reaction_like, R.id.menu_reaction_dislike,
                R.id.menu_reaction_love};

        var contextThemeWrapper = new ContextThemeWrapper(mainActivity,
                R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_reactions);

        popupMenu.show();

        var menu = popupMenu.getMenu();

        for (int id : menuIds)
        {
            menu.findItem(id).setOnMenuItemClickListener(item ->
            {
                binding.reactionsRecyclerView.setVisibility(View.VISIBLE);
                String emojiItem = item.getTitle().toString();
                if (reactionsAdapter == null)
                {
                    initAdapterForReactionsRecyclerView();
                }
                reactionsAdapter.addElement(new Reaction(emojiItem, new ArrayList<>()));
                return true;
            });
        }

        return true;
    }

    private void initReactionsRecyclerView()
    {
        var reactionsRecyclerView = binding.reactionsRecyclerView;
        reactionsRecyclerView.setLayoutManager(
                new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        if (reactionsAdapter != null)
        {
            reactionsRecyclerView.setAdapter(reactionsAdapter);
            reactionsRecyclerView.setVisibility(View.VISIBLE);
        }
        else
        {
            initAdapterForReactionsRecyclerView();
        }
    }

    private void initAdapterForReactionsRecyclerView()
    {
        reactionsAdapter = new ReactionsAdapter(mainActivity, postItemHelper.getReactions(),
                postItemHelper.post);
        binding.reactionsRecyclerView.setAdapter(reactionsAdapter);
    }

    private void initShowCommentsButton()
    {
        var showCommentsButton = binding.showCommentsButton;
        showCommentsButton.setOnClickListener(this::onCommentViewClick);
    }

    private void onCommentViewClick(View view)
    {
        var commentsFragment = new CommentsFragment();
        commentsFragment.setPostItem(this);
//        Navigator.goToFragment(commentsFragment);
    }

    public View getShowReactionsButton()
    {
        return binding.showReactionsButton;
    }

    public View getShowCommentsButton()
    {
        return binding.showCommentsButton;
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
        if (menu != null)
        {
            menu.findItem(menuItemId).setVisible(visible);
        }
    }

    public class PostItemHelper extends DateString implements HavingCommentsIds
    {
        public static final int POST_ONLY_TEXT = 0, POST_ONLY_IMAGES = 1, POST_FULL = 2;

        private final PostImpl post;

        private final ObservableField<String> title;

        private final MyObservableArrayList<String> commentIds;
        private final MyObservableArrayList<String> positiveRates;
        private final MyObservableArrayList<String> negativeRates;

        private final ObservableInt commentsCount;
        private final ObservableInt ratesCount;

        private final int type;

        private MarkerInfoItem.MarkerInfoItemHelper markerInfoItemHelper;

        public PostItemHelper(PostImpl post)
        {
            super(mainActivity);
            this.post = post;

            this.title = new ObservableField<>(post.getTitle());

            this.commentIds = new MyObservableArrayList<>(post.getCommentIds());
            this.positiveRates = new MyObservableArrayList<>(post.getPositiveRates());
            this.negativeRates = new MyObservableArrayList<>(post.getNegativeRates());

            commentsCount = new ObservableInt(commentIds.size());
            ratesCount = new ObservableInt(positiveRates.size() - negativeRates.size());

            this.commentIds.setOnListChangedCallback(new PostImpl.Operation(commentsCount, 1, -1));
            this.positiveRates.setOnListChangedCallback(new PostImpl.Operation(ratesCount, 1, -1));
            this.negativeRates.setOnListChangedCallback(new PostImpl.Operation(ratesCount, -1, 1));

            if (post.getImages() == null || post.getImages().isEmpty())
            {
                type = POST_ONLY_TEXT;
            }
            else if (post.getText() == null || post.getText().isEmpty())
            {
                type = POST_ONLY_IMAGES;
            }
            else
            {
                type = POST_FULL;
            }
        }

        public boolean copyText()
        {
            var clipboard = (ClipboardManager) mainActivity.getSystemService(
                    Context.CLIPBOARD_SERVICE);
            var clip = ClipData.newPlainText("simple text", post.getText());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean forward()
        {
            Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean goTo()
        {
            if (markerInfoItemHelper != null)
            {
                markerInfoItemHelper.goTo();
            }

            return true;
        }

        public boolean edit()
        {
            Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean delete()
        {
            if (markerInfoItemHelper != null)
            {
                markerInfoItemHelper.delete();
            }
            postsAdapter.deletePostItem(PostItem.this);

            mainActivity.appDatabase.postDao().delete(post);
            mainActivity.mobServerAPI.postDelete(new MOBAPICallbackImpl(), post.getId(),
                    MainActivity.token);

            Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG).show();

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

        public ObservableField<String> getTitle()
        {
            return title;
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
            return commentIds;
        }

        public List<String> getPositiveRates()
        {
            return positiveRates;
        }

        public List<String> getNegativeRates()
        {
            return negativeRates;
        }

        public ObservableInt getCommentsCount()
        {
            return commentsCount;
        }

        public ObservableInt getRatesCount()
        {
            return ratesCount;
        }

        public int getType()
        {
            return type;
        }
    }
}
