package com.example.mobv2.ui.views.items;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

import com.example.mobv2.R;
import com.example.mobv2.adapters.ReactionsAdapter;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.models.abstractions.UserContent;
import com.example.mobv2.ui.abstractions.Item;
import com.example.mobv2.ui.activities.MainActivity;

import java.util.List;

import localdatabase.daos.UserDao;

public class ReactionItem implements Item<ItemReactionBinding>
{
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private final ReactionsAdapter reactionsAdapter;

    public final ReactionItemHelper reactionItemHelper;

    private ItemReactionBinding reactionBinding;

    public ReactionItem(MainActivity mainActivity,
                        ReactionsAdapter reactionsAdapter,
                        Reaction reaction)
    {
        this.mainActivity = mainActivity;
        this.reactionsAdapter = reactionsAdapter;
        this.reactionItemHelper = new ReactionItemHelper(reaction);

        userDao = mainActivity.appDatabase.userDao();
    }

    @Override
    public void refreshItemBinding(@NonNull ItemReactionBinding reactionBinding)
    {
        this.reactionBinding = reactionBinding;
        View parentView = reactionBinding.getRoot();

        List<String> userIdsWhoLiked = reactionItemHelper.getUserIdsWhoLiked();
        String userId = userDao.getCurrentId();

        reactionBinding.setCount(reactionItemHelper.getCount());

        reactionBinding.imageReactionView.setText(reactionItemHelper.getEmoji());

        parentView.setOnClickListener(view -> toggleChecked());
        parentView.setBackgroundResource(0);

        if (userIdsWhoLiked.contains(userId))
        {
            reactionItemHelper.setChecked(true);
        }

        if (reactionItemHelper.isChecked())
        {
            parentView.setSelected(true);
            parentView.setBackgroundResource(R.drawable.background_item_reaction_selected);
        }
    }

    public void toggleChecked()
    {
        String userId = userDao.getCurrentId();

        if (reactionItemHelper.isChecked())
        {
            reactionItemHelper.down(userId);
        }
        else
        {
            reactionItemHelper.up(userId);
        }

        reactionsAdapter.notifyItemChanged(this);

        if (reactionItemHelper.getCount()
                              .get() == 0)
        {
            reactionItemHelper.delete();
        }
    }

    public class ReactionItemHelper
    {
        private final Reaction reaction;

        private UserContent userContent;

        public ReactionItemHelper(Reaction reaction)
        {
            this.reaction = reaction;
        }

        public void up(String userId)
        {
            if (userContent == null) return;

            getUserIdsWhoLiked().add(userId);
            reaction.setChecked(true);
            if (userContent instanceof PostImpl)
            {
                mainActivity.mobServerAPI.postReact(new MOBAPICallbackImpl(), userContent.getId(), getEmoji(), MainActivity.token);
            }
            else if (userContent instanceof CommentImpl)
            {
                mainActivity.mobServerAPI.commentReact(new MOBAPICallbackImpl(), userContent.getId(), getEmoji(), MainActivity.token);
            }
        }

        public void down(String userId)
        {
            if (userContent == null) return;

            getUserIdsWhoLiked().remove(userId);
            reaction.setChecked(false);
            if (userContent instanceof PostImpl)
            {
                mainActivity.mobServerAPI.postUnreact(new MOBAPICallbackImpl(), userContent.getId(), getEmoji(), MainActivity.token);
            }
            else if (userContent instanceof CommentImpl)
            {
                mainActivity.mobServerAPI.commentUnreact(new MOBAPICallbackImpl(), userContent.getId(), getEmoji(), MainActivity.token);
            }
        }

        public void delete()
        {
            reactionsAdapter.deleteReactionItem(ReactionItem.this);
        }

        public void setUserContent(UserContent userContent)
        {
            this.userContent = userContent;
        }

        public String getEmoji()
        {
            return reaction.getEmoji();
        }

        public List<String> getUserIdsWhoLiked()
        {
            return reaction.getUserIdsWhoLiked();
        }

        public ObservableInt getCount()
        {
            return reaction.getCount();
        }

        public void setChecked(boolean checked)
        {
            reaction.setChecked(checked);
        }

        public boolean isChecked()
        {
            return reaction.isChecked();
        }
    }
}
