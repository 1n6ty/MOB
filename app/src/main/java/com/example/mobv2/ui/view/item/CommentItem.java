package com.example.mobv2.ui.view.item;

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
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.inputMessage.InputMessageFragment;
import com.example.mobv2.ui.fragment.inputMessage.InputMessageFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import localDatabase.dao.CommentDao;
import localDatabase.dao.UserDao;

public class CommentItem implements Item<ItemCommentBinding>, CommentOkCallback
{
    private final CommentDao commentDao;
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private final CommentsAdapter commentsAdapter;
    private final ReactionsAdapter reactionsAdapter;
    private CommentsAdapter innerCommentsAdapter;

    public final CommentItemHelper commentItemHelper;

    private ItemCommentBinding commentBinding;

    private Menu menu;

    public CommentItem(MainActivity mainActivity,
                       CommentsAdapter commentsAdapter,
                       CommentImpl comment)
    {
        this.mainActivity = mainActivity;
        this.commentsAdapter = commentsAdapter;
        this.commentItemHelper = new CommentItemHelper(comment);
        reactionsAdapter =
                new ReactionsAdapter(mainActivity, commentItemHelper.getReactions(), commentItemHelper.comment);

        commentDao = mainActivity.appDatabase.commentDao();
        userDao = mainActivity.appDatabase.userDao();
    }

    @Override
    public void refreshItemBinding(@NonNull ItemCommentBinding commentBinding)
    {
        this.commentBinding = commentBinding;
        var parentView = commentBinding.getRoot();

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
        var parentView = commentBinding.getRoot();

        var user = commentItemHelper.getUser();

        commentBinding.setFullName(user.getFullName());
        commentBinding.setDate(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(commentItemHelper.getDate()));
        commentBinding.setCommentText(commentItemHelper.getText());
        commentBinding.setRatesCount(commentItemHelper.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, commentBinding.avatarView);
    }

    private void onItemViewClick(View view)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, commentBinding.userInfoLayout);
        popupMenu.inflate(R.menu.menu_item_comment);

        initMenu(popupMenu);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        menu = popupMenu.getMenu();
        var user = commentItemHelper.getUser();

        boolean isCreator =
                user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
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
        commentBinding.closeCommentsButton.setOnClickListener(this::onCloseCommentsButtonClick);
    }

    private void onCloseCommentsButtonClick(View view)
    {
        view.setVisibility(View.GONE);
        commentBinding.showCommentsButton.setVisibility(View.VISIBLE);

        commentBinding.commentsRecyclerView.setVisibility(View.GONE);
    }

    private void initRatesGroup()
    {
        var ratesGroup = commentBinding.ratesGroup;

        var user = commentItemHelper.getUser();
        var userId = user.getId();

        ratesGroup.getRateUpButton()
                  .setSelected(false);
        ratesGroup.getRateDownButton()
                  .setSelected(false);

        if (commentItemHelper.getPositiveRates()
                             .contains(userId))
        {
            ratesGroup.getRateUpButton()
                      .setSelected(true);
        }
        else if (commentItemHelper.getNegativeRates()
                                  .contains(userId))
        {
            ratesGroup.getRateDownButton()
                      .setSelected(true);
        }

        ratesGroup.setOnRateUpClickListener(this::onRateUpButtonClick);
        ratesGroup.setOnRateDownClickListener(this::onRateDownButtonClick);
    }

    private void onRateUpButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(commentItemHelper.getNegativeRates(), commentItemHelper.getPositiveRates());

        mainActivity.mobServerAPI.commentInc(new MOBAPICallbackImpl(), commentItemHelper.getId(), MainActivity.token);
    }

    private void onRateDownButtonClick(View view)
    {
        removeRateFromFirstRatesAndAddRateToSecondRates(commentItemHelper.getPositiveRates(), commentItemHelper.getNegativeRates());

        mainActivity.mobServerAPI.commentDec(new MOBAPICallbackImpl(), commentItemHelper.getId(), MainActivity.token);
    }

    private void removeRateFromFirstRatesAndAddRateToSecondRates(List<String> firstRates,
                                                                 List<String> secondRates)
    {
        var userId = userDao.getCurrentId();
        firstRates.remove(userId);
        if (!secondRates.remove(userId)) secondRates.add(userId);
    }

    private void initReplyButton()
    {
        var replyButton = commentBinding.replyButton;
        replyButton.setOnClickListener(this::onReplyButtonClick);
    }

    private void onReplyButtonClick(View view)
    {
        var viewModel =
                new ViewModelProvider(mainActivity).get(InputMessageFragmentViewModel.class);

        viewModel.setParentId(commentItemHelper.getId());
        viewModel.setCreateCommentByIdAndAddToCommentIds(this::createCommentByIdAndTextAndAddToCommentIds);
        if (!viewModel.getActive())
        {
            mainActivity.goToFragment(new InputMessageFragment(), 0, 0);
        }
    }

    @Override
    public void createCommentByIdAndTextAndAddToCommentIds(String commentId,
                                                           String messageText)
    {
        var comment = CommentImpl.createNewComment(commentId, userDao.getCurrentOne(), messageText);
        if (innerCommentsAdapter == null) initAdapterForInnerCommentsRecyclerView();
        innerCommentsAdapter.addElement(comment);
        commentItemHelper.getCommentIds()
                         .add(commentId);
    }

    private void initShowReactionsButton()
    {
        var showReactionsButton = commentBinding.showReactionsButton;

        if (reactionsAdapter != null)
            commentBinding.reactionsRecyclerView.setVisibility(View.VISIBLE);

        showReactionsButton.setOnClickListener(this::onShowReactionsViewClick);
        showReactionsButton.setOnLongClickListener(this::onShowReactionsViewLongClick);
    }

    private void onShowReactionsViewClick(View view)
    {
        RecyclerView reactionsRecyclerView = commentBinding.reactionsRecyclerView;
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
                    reactionsAdapter.addElement(new Reaction(emojiItem, new ArrayList<>()));
                    return true;
                });
        }

        return true;
    }

    private void initReactionsRecyclerView()
    {
        var reactionsRecyclerView = commentBinding.reactionsRecyclerView;
        reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        if (reactionsAdapter != null) reactionsRecyclerView.setAdapter(reactionsAdapter);
    }

    private void initShowCommentsButton()
    {
        var showCommentsButton = commentBinding.showCommentsButton;

        if (innerCommentsAdapter != null)
        {
            showCommentsButton.setVisibility(View.GONE);
        }
        else
        {
            showCommentsButton.setVisibility(commentItemHelper.getCommentsCount()
                                                              .get() > 0
                    ? View.VISIBLE
                    : View.GONE);
        }

        showCommentsButton.setOnClickListener(this::onShowCommentButtonClick);
    }

    private void onShowCommentButtonClick(View view)
    {
        view.setVisibility(View.GONE);
        commentBinding.closeCommentsButton.setVisibility(View.VISIBLE);

        commentBinding.commentsRecyclerView.setVisibility(View.VISIBLE);

        if (innerCommentsAdapter == null) initAdapterForInnerCommentsRecyclerView();
    }

    private void initAdapterForInnerCommentsRecyclerView()
    {
        innerCommentsAdapter =
                new CommentsAdapter(mainActivity, commentBinding.nestedScrollView, commentItemHelper);
        commentBinding.commentsRecyclerView.setAdapter(innerCommentsAdapter);
    }

    private void initCommentsRecyclerView()
    {
        var innerCommentsRecyclerView = commentBinding.commentsRecyclerView;
        innerCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        if (innerCommentsAdapter != null)
            innerCommentsRecyclerView.setAdapter(innerCommentsAdapter);
    }

    public class CommentItemHelper implements HavingCommentsIds
    {
        private final CommentImpl comment;

        private HavingCommentsIds havingCommentsIds;

        public CommentItemHelper(CommentImpl comment)
        {
            this.comment = comment;
        }

        public boolean copyText()
        {
            var clipboard =
                    (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            var clip = ClipData.newPlainText("simple text", comment.getText());
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

        public boolean edit()
        {
            Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
                 .show();
            return true;
        }

        public boolean delete()
        {
            if (havingCommentsIds != null)
            {
                havingCommentsIds.getCommentIds()
                                 .remove(comment.getId());
            }
            commentsAdapter.deleteComment(CommentItem.this);

            commentDao.delete(comment);
            mainActivity.mobServerAPI.commentDelete(new MOBAPICallbackImpl(), comment.getId(), MainActivity.token);

            Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
                 .show();

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

    private void switchMenuItemVisibility(int menuItemId,
                                          boolean visible)
    {
        if (menu != null)
        {
            menu.findItem(menuItemId)
                .setVisible(visible);
        }
    }
}
