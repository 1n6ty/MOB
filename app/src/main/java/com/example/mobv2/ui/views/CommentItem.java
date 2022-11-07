package com.example.mobv2.ui.views;

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

import com.example.mobv2.R;
import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.adapters.ReactionsCommentAdapter;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.models.abstractions.HavingCommentsIds;
import com.example.mobv2.ui.abstractions.Item;
import com.example.mobv2.ui.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import localdatabase.daos.CommentDao;
import localdatabase.daos.UserDao;

public class CommentItem implements Item<ItemCommentBinding>
{
    private final CommentDao commentDao;
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private final CommentsAdapter commentsAdapter;
    private final ReactionsCommentAdapter reactionsAdapter;

    public CommentItemHelper commentItemHelper;

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
                new ReactionsCommentAdapter(mainActivity, commentItemHelper.getReactions(), commentItemHelper.getId());

        commentDao = mainActivity.appDatabase.commentDao();
        userDao = mainActivity.appDatabase.userDao();
    }

    @Override
    public void refreshItemBinding(@NonNull ItemCommentBinding commentBinding)
    {
        this.commentBinding = commentBinding;
        var parentView = commentBinding.getRoot();

        var user = commentItemHelper.getUser();
        var userId = user.getId();

        parentView.setOnClickListener(this::onItemViewClick);

        commentBinding.setRatesCount(commentItemHelper.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), parentView, commentBinding.avatarView);

        commentBinding.fullNameView.setText(user.getFullName());

        commentBinding.commentTextView.setText(commentItemHelper.getText());

        commentBinding.dateView.setText(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(commentItemHelper.getDate()));

        if (commentItemHelper.getPositiveRates()
                             .contains(userId))
        {
            commentBinding.ratesGroup.getRateUpButton()
                                     .setSelected(true);
        }
        else if (commentItemHelper.getNegativeRates()
                                  .contains(userId))
        {
            commentBinding.ratesGroup.getRateDownButton()
                                     .setSelected(true);
        }

        commentBinding.ratesGroup.setOnRateUpClickListener(this::onRateUpButtonClick);
        commentBinding.ratesGroup.setOnRateDownClickListener(this::onRateDownButtonClick);

        commentBinding.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(commentBinding.reactionsRecyclerView));
        commentBinding.showReactionsView.setOnLongClickListener(this::onShowReactionsViewLongClick);

        commentBinding.reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        commentBinding.reactionsRecyclerView.setAdapter(reactionsAdapter);

        commentBinding.showCommentsButton.setVisibility(commentItemHelper.getCommentsCount()
                                                                         .get() > 0
                ? View.VISIBLE
                : View.GONE);
        commentBinding.showCommentsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void onItemViewClick(View view)
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
        var user = commentItemHelper.getUser();

        boolean isCreator =
                user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
        menu.findItem(R.id.menu_edit)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete)
            .setVisible(isCreator);

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

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
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
            if (havingCommentsIds != null) havingCommentsIds.getCommentIds()
                                                            .remove(comment.getId());
            commentsAdapter.deleteComment(CommentItem.this);

            // delete from local db
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
}
