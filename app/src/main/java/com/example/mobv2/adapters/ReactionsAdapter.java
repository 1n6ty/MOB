package com.example.mobv2.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.CommentChangeReactCallback;
import com.example.mobv2.callbacks.PostChangeReactCallback;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.utils.MyObservableArrayList;

import java.util.ArrayList;
import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>
{
    private final MainActivity mainActivity;
    private final MyObservableArrayList<ReactionItem> reactionItems;

    private final int postId;
    private final int commentId;
    private final boolean ownerIsPost;

    public ReactionsAdapter(MainActivity mainActivity,
                            List<Reaction> reactions,
                            int postId)
    {
        this(mainActivity, reactions, postId, -1, true);
    }

    public ReactionsAdapter(MainActivity mainActivity,
                            List<Reaction> reactions,
                            int postId,
                            int commentId)
    {
        this(mainActivity, reactions, postId, commentId, false);
    }

    private ReactionsAdapter(MainActivity mainActivity,
                             List<Reaction> reactions,
                             int postId,
                             int commentId,
                             boolean ownerIsPost)
    {
        this.mainActivity = mainActivity;
        this.reactionItems = new MyObservableArrayList<>();


        this.postId = postId;
        this.commentId = commentId;
        this.ownerIsPost = ownerIsPost;

        for (Reaction reaction : reactions)
        {
            reactionItems.add(new ReactionItem(reaction));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            reactionItems.sort((item1, item2) -> Integer.compare(item2.getReaction()
                                                                      .getCount(), item1.getReaction()
                                                                                        .getCount()));
        }

        this.reactionItems.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(ReactionItem reactionItem)
            {
                reactions.add(reactionItem.getReaction());
            }

            @Override
            public void onAdded(int index,
                                ReactionItem element)
            {
                reactions.add(index, element.getReaction());
            }

            @Override
            public void onRemoved(int index)
            {
                reactions.remove(index);
            }

            @Override
            public void onRemoved(@Nullable Object o)
            {
                reactions.remove(o);
            }
        });
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

        holder.reaction.setText(reaction.getEmoji());

        holder.count.setText(String.valueOf(reaction.getCount()));

        holder.itemView.setOnClickListener(view -> toggleChecked(holder.getAdapterPosition()));
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

        //TODO change
        reactionItems.add(new ReactionItem(new Reaction(emoji, usersWhoLiked), true));
        if (ownerIsPost)
            MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
        else
            MainActivity.MOB_SERVER_API.commentReact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        notifyItemInserted(reactionItems.size() - 1);
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
        String emoji = reaction.getEmoji();
        if (reactionItem.isChecked())
        {
            //TODO change
            reactionItem.setChecked(false);
            userIdsWhoLiked.remove((Object) userId);
            if (ownerIsPost)
                MainActivity.MOB_SERVER_API.postUnreact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
            else
                MainActivity.MOB_SERVER_API.commentUnreact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        }
        else
        {
            //TODO change
            reactionItem.setChecked(true);
            userIdsWhoLiked.add(userId);
            if (ownerIsPost)
                MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
            else
                MainActivity.MOB_SERVER_API.commentReact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        }

        //TODO will be remade
        if (reaction.getCount() == 0)
        {
            reactionItems.remove(position);
            notifyItemRemoved(position);
        }
        else
        {
            for (int i = 0; i < reactionItems.size(); i++)
            {
                Reaction currentReaction = reactionItems.get(i)
                                                        .getReaction();
                if (reaction.getCount() >= currentReaction.getCount())
                {
                    reactionItems.remove(position);
                    notifyItemRemoved(position);
                    reactionItems.add(i, new ReactionItem(reaction));
                    notifyItemInserted(i);
                    break;
                }
            }
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
