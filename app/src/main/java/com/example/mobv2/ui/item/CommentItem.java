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
import androidx.databinding.ObservableInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.CommentsAdapter;
import com.example.mobv2.adapter.ReactionsAdapter;
import com.example.mobv2.callback.MOBAPICallbackImpl;
import com.example.mobv2.callback.abstraction.CommentOkCallback;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.model.CommentImpl;
import com.example.mobv2.model.Reaction;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.model.abstraction.HavingCommentsIds;
import com.example.mobv2.ui.abstraction.Item;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.fragment.InputMessageFragment;
import com.example.mobv2.util.DateString;
import com.example.mobv2.util.Navigator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentItem implements Item<ItemCommentBinding>, CommentOkCallback
{
    public final CommentItemHelper commentItemHelper;
    private final MainActivity mainActivity;
    private final CommentsAdapter commentsAdapter;
    private ReactionsAdapter reactionsAdapter;
    private CommentsAdapter innerCommentsAdapter;
    private ItemCommentBinding binding;

    private Menu menu;

    public CommentItem(MainActivity mainActivity,
                       CommentsAdapter commentsAdapter,
                       CommentImpl comment)
    {
        this.mainActivity = mainActivity;
        this.commentsAdapter = commentsAdapter;
        this.commentItemHelper = new CommentItemHelper(comment);
    }

    @Override
    public void refreshItemBinding(@NonNull ItemCommentBinding binding)
    {
        this.binding = binding;
        var parentView = binding.getRoot();

        initInfo();

        parentView.setOnClickListener(this::onItemViewClick);

        initCloseCommentsButton();
        initRatesGroup();
        initReplyButton();
        initShowReactionsButton();
        initReactionsRecyclerView();
        initShowCommentsButton();
        initCommentsRecyclerView();
    }

    private void initInfo()
    {
        var parentView = binding.getRoot();

        var user = commentItemHelper.getUser();

        binding.setFullName(user.getFullName());
        binding.setDate(commentItemHelper.getDateString());
        binding.setCommentText(commentItemHelper.getText());
        binding.setRatesCount(commentItemHelper.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, binding.avatarView);
    }

    private void onItemViewClick(View view)
    {
        var contextThemeWrapper = new ContextThemeWrapper(mainActivity,
                R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, binding.userInfoLayout);
        popupMenu.inflate(R.menu.menu_item_comment);

        initMenu(popupMenu);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        menu = popupMenu.getMenu();
        var user = commentItemHelper.getUser();

        var currentUser = mainActivity.appDatabase.userDao().getCurrentOne();

        boolean isCreator = user.compareById(currentUser);  // if the user is a post's creator
        switchMenuItemVisibility(R.id.menu_edit, isCreator);
        switchMenuItemVisibility(R.id.menu_delete, isCreator);

        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_copy_text:
                return commentItemHelper.copyText();
            case R.id.menu_forward:
                return commentItemHelper.forward();
            case R.id.menu_edit:
                return commentItemHelper.edit();
            case R.id.menu_delete:
                return commentItemHelper.delete();
            default:
                return false;
        }
    }

    private void initCloseCommentsButton()
    {
        binding.closeCommentsButton.setOnClickListener(this::onCloseCommentsButtonClick);
    }

    private void onCloseCommentsButtonClick(View view)
    {
        view.setVisibility(View.GONE);
        binding.showCommentsButton.setVisibility(View.VISIBLE);

        binding.commentsRecyclerView.setVisibility(View.GONE);
    }

    private void initRatesGroup()
    {
        var ratesGroup = binding.ratesGroup;

        var user = commentItemHelper.getUser();
        var userId = user.getId();

        ratesGroup.getRateUpButton().setSelected(false);
        ratesGroup.getRateDownButton().setSelected(false);

        if (commentItemHelper.getPositiveRates().contains(userId))
        {
            ratesGroup.getRateUpButton().setSelected(true);
        }
        else if (commentItemHelper.getNegativeRates().contains(userId))
        {
            ratesGroup.getRateDownButton().setSelected(true);
        }

        ratesGroup.setOnRateUpClickListener(this::onRateUpButtonClick);
        ratesGroup.setOnRateDownClickListener(this::onRateDownButtonClick);
    }

    private void onRateUpButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(commentItemHelper.getNegativeRates(),
                commentItemHelper.getPositiveRates());

        mainActivity.mobServerAPI.commentInc(new MOBAPICallbackImpl(), commentItemHelper.getId(),
                MainActivity.token);
    }

    private void onRateDownButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(commentItemHelper.getPositiveRates(),
                commentItemHelper.getNegativeRates());

        mainActivity.mobServerAPI.commentDec(new MOBAPICallbackImpl(), commentItemHelper.getId(),
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

    private void initReplyButton()
    {
        var replyButton = binding.replyButton;
        replyButton.setOnClickListener(this::onReplyButtonClick);
    }

    private void onReplyButtonClick(View view)
    {
        if (!InputMessageFragment.active)
        {
            var inputMessageFragment = new InputMessageFragment();
            inputMessageFragment.setParentId(commentItemHelper.getId());
            inputMessageFragment.setCommentOkCallback(
                    this::createCommentByIdAndTextAndAddToCommentIds);
            Navigator.goToFragment(inputMessageFragment);
        }
    }

    @Override
    public void createCommentByIdAndTextAndAddToCommentIds(String commentId,
                                                           String messageText)
    {
        var comment = CommentImpl.createNewComment(commentId,
                mainActivity.appDatabase.userDao().getCurrentOne(), messageText);

        mainActivity.appDatabase.commentDao().insert(comment);
        innerCommentsAdapter.addElement(comment);
        commentItemHelper.getCommentIds().add(commentId);

        binding.showCommentsButton.callOnClick();
//        binding.commentsRecyclerView.scrollTo(binding.commentsRecyclerView.get);
    }

    private void initShowReactionsButton()
    {
        var showReactionsButton = binding.showReactionsButton;

        showReactionsButton.setOnClickListener(this::onShowReactionsViewClick);
        showReactionsButton.setOnLongClickListener(this::onShowReactionsViewLongClick);
    }

    private void onShowReactionsViewClick(View view)
    {
        RecyclerView reactionsRecyclerView = binding.reactionsRecyclerView;

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
        reactionsAdapter = new ReactionsAdapter(mainActivity, commentItemHelper.getReactions(),
                commentItemHelper.comment);
        binding.reactionsRecyclerView.setAdapter(reactionsAdapter);
    }

    private void initShowCommentsButton()
    {
        var showCommentsButton = binding.showCommentsButton;

        if (innerCommentsAdapter != null)
        {
            showCommentsButton.setVisibility(View.GONE);
        }
        else
        {
            showCommentsButton.setVisibility(
                    commentItemHelper.getCommentsCount().get() > 0 ? View.VISIBLE : View.GONE);
        }

        showCommentsButton.setOnClickListener(this::onShowCommentButtonClick);
    }

    private void onShowCommentButtonClick(View view)
    {
        view.setVisibility(View.GONE);
        binding.closeCommentsButton.setVisibility(View.VISIBLE);

        binding.commentsRecyclerView.setVisibility(View.VISIBLE);

        if (innerCommentsAdapter == null)
        {
            initAdapterForInnerCommentsRecyclerView();
        }
    }

    private void initAdapterForInnerCommentsRecyclerView()
    {
        innerCommentsAdapter = new CommentsAdapter(mainActivity, binding.nestedScrollView,
                commentItemHelper);
        binding.commentsRecyclerView.setAdapter(innerCommentsAdapter);
    }

    private void initCommentsRecyclerView()
    {
        var innerCommentsRecyclerView = binding.commentsRecyclerView;
        innerCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        if (innerCommentsAdapter != null)
        {
            innerCommentsRecyclerView.setAdapter(innerCommentsAdapter);
        }
    }

    private void switchMenuItemVisibility(int menuItemId,
                                          boolean visible)
    {
        if (menu != null)
        {
            menu.findItem(menuItemId).setVisible(visible);
        }
    }

    public class CommentItemHelper extends DateString implements HavingCommentsIds
    {
        private final CommentImpl comment;

        private HavingCommentsIds havingCommentsIds;

        public CommentItemHelper(CommentImpl comment)
        {
            super(mainActivity);
            this.comment = comment;
        }

        public boolean copyText()
        {
            var clipboard = (ClipboardManager) mainActivity.getSystemService(
                    Context.CLIPBOARD_SERVICE);
            var clip = ClipData.newPlainText("simple text", comment.getText());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean forward()
        {
            Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean edit()
        {
            Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG).show();
            return true;
        }

        public boolean delete()
        {
            if (havingCommentsIds != null)
            {
                havingCommentsIds.getCommentIds().remove(comment.getId());
            }
            commentsAdapter.deleteComment(CommentItem.this);

            mainActivity.appDatabase.commentDao().delete(comment);
            mainActivity.mobServerAPI.commentDelete(new MOBAPICallbackImpl(), comment.getId(),
                    MainActivity.token);

            Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG).show();

            return true;
        }

        public void setHavingCommentsIds(HavingCommentsIds havingCommentsIds)
        {
            this.havingCommentsIds = havingCommentsIds;
        }

        public String getId()
        {
            return comment.getId();
        }

        public UserImpl getUser()
        {
            return comment.getUser();
        }

        public Date getDate()
        {
            return comment.getDate();
        }

        public String getText()
        {
            return comment.getText();
        }

        public List<Reaction> getReactions()
        {
            return comment.getReactions();
        }

        @Override
        public List<String> getCommentIds()
        {
            return comment.getCommentIds();
        }

        public List<String> getPositiveRates()
        {
            return comment.getPositiveRates();
        }

        public List<String> getNegativeRates()
        {
            return comment.getNegativeRates();
        }

        public ObservableInt getCommentsCount()
        {
            return comment.getCommentsCount();
        }

        public ObservableInt getRatesCount()
        {
            return comment.getRatesCount();
        }
    }
}
