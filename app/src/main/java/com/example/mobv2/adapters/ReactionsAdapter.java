package com.example.mobv2.adapters;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.PostChangeReactCallback;

import java.util.ArrayList;
import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>
{
    private final MainActivity mainActivity;
    private final List<ReactionItem> reactionItems;

    private final int postId;

    public ReactionsAdapter(MainActivity mainActivity,
                            List<Reaction> reactions,
                            int postId)
    {
        this.mainActivity = mainActivity;
        this.reactionItems = new ArrayList<>();

        this.postId = postId;

        for (Reaction reaction : reactions)
        {
            reactionItems.add(new ReactionItem(reaction));
        }

        reactionItems.add(new ReactionItem(Reaction.createAdd()));
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType)
    {
        View reactionItem = LayoutInflater.from(parent.getContext())
                                          .inflate(R.layout.item_reaction, parent, false);
        return new ReactionViewHolder(reactionItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder,
                                 int position)
    {
        ReactionItem reactionItem = reactionItems.get(holder.getAdapterPosition());
        Reaction reaction = reactionItem.getReaction();
        List<Integer> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        int userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);

        final boolean isAddButton = reaction.isAdd();

        holder.reaction.setText(reaction.getEmoji());

        holder.count.setText(String.valueOf(reaction.getCount()));
        holder.count.setVisibility(isAddButton ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(view -> onReactionItemClicked(view, isAddButton, holder.getAdapterPosition()));
        holder.itemView.setBackgroundResource(0);

        if (userIdsWhoLiked != null && userIdsWhoLiked.contains(userId))
        {
            reactionItem.setChecked(true);
        }


        if (reactionItem.isChecked())
        {
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.background_item_reaction_selected);
        }
    }

    private void onReactionItemClicked(View view,
                                       boolean isAddButton,
                                       int position)
    {
        if (isAddButton)
        {
            Context contextThemeWrapper =
                    new ContextThemeWrapper(mainActivity, R.style.MyPopupOtherStyle);
            PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, view, Gravity.NO_GRAVITY);
            popupMenu.inflate(R.menu.menu_reactions);

            initMenuAdd(popupMenu);
            // inflate menu
            popupMenu.show();
        }
        else
            toggleChecked(position);
    }

    private void initMenuAdd(@NonNull PopupMenu popupMenu)
    {
        Menu menu = popupMenu.getMenu();

        int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item -> addReaction(item.getTitle()
                                                                    .toString()));
        }
    }

    public boolean addReaction(String emoji)
    {
        for (int i = 0; i < reactionItems.size(); i++)
        {
            String currentEmoji = reactionItems.get(i)
                                               .getReaction()
                                               .getEmoji();
            if (currentEmoji.equals(emoji))
            {
                toggleChecked(i);
                return false;
            }
        }

        List<Integer> usersWhoLiked = new ArrayList<>();
        int userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);
        usersWhoLiked.add(userId);

        reactionItems.add(reactionItems.size() - 1, new ReactionItem(new Reaction(emoji, usersWhoLiked), true));
        MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
        notifyItemInserted(reactionItems.size() - 2);
        return true;
    }

    private void toggleChecked(int position)
    {
        // half-measure

        ReactionItem reactionItem = reactionItems.get(position);
        Reaction reaction = reactionItem.getReaction();

        int userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);
        List<Integer> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        if (reactionItem.isChecked())
        {
            reactionItem.setChecked(false);
            userIdsWhoLiked.remove((Object) userId);
            MainActivity.MOB_SERVER_API.postUnreact(new PostChangeReactCallback(), postId, reaction.getEmoji(), MainActivity.token);
        }
        else
        {
            reactionItem.setChecked(true);
            userIdsWhoLiked.add(userId);
            MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, reaction.getEmoji(), MainActivity.token);
        }

        if (reaction.getCount() == 0)
        {
            reactionItems.remove(position);
            notifyItemRemoved(position);
        }

        sortReactionItems();
    }

    private void sortReactionItems()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            reactionItems.sort((item1, item2) -> Integer.compare(item2.getReaction()
                                                                      .getCount(), item1.getReaction()
                                                                                        .getCount()));
            notifyItemRangeChanged(0, reactionItems.size() - 1);
        }
    }

    @Override
    public int getItemCount()
    {
        return reactionItems.size();
    }

    protected static class ReactionViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView reaction;
        private final TextView count;

        public ReactionViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemReactionBinding binding = ItemReactionBinding.bind(itemView);
            reaction = binding.reaction;
            count = binding.count;
        }
    }

    protected static class ReactionItem
    {
        private final Reaction reaction;
        private boolean checked;

        public ReactionItem(Reaction reaction)
        {
            this.reaction = reaction;
            this.checked = false;
        }

        public ReactionItem(Reaction reaction,
                            boolean checked)
        {
            this.reaction = reaction;
            this.checked = checked;
        }

        public Reaction getReaction()
        {
            return reaction;
        }

        public void setChecked(boolean checked)
        {
            this.checked = checked;
        }

        public boolean isChecked()
        {
            return checked;
        }
    }
}
