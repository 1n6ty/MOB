package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.CommentChangeReactCallback;
import com.example.mobv2.callbacks.PostChangeReactCallback;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>
{
    private final MainActivity mainActivity;
    private final List<Reaction> reactions;

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
        this.reactions = reactions;


        this.postId = postId;
        this.commentId = commentId;
        this.ownerIsPost = ownerIsPost;

//        for (Reaction reaction : reactions)
//        {
//            reactionItems.add(new Reaction(reaction));
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//        {
//            reactionItems.sort((item1, item2) -> Integer.compare(item2.getReaction()
//                                                                      .getCount(), item1.getReaction()
//                                                                                        .getCount()));
//        }
//
//        this.reactionItems.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
//        {
//            @Override
//            public void onAdded(Reaction reactionItem)
//            {
//                reactions.add(reactionItem.getReaction());
//            }
//
//            @Override
//            public void onAdded(int index,
//                                Reaction element)
//            {
//                reactions.add(index, element.getReaction());
//            }
//
//            @Override
//            public void onRemoved(int index)
//            {
//                reactions.remove(index);
//            }
//
//            @Override
//            public void onRemoved(@Nullable Object o)
//            {
//                reactions.remove(o);
//            }
//        });
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
        Reaction reaction = reactions.get(holder.getAdapterPosition());
        List<Integer> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        int userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);

        holder.reaction.setText(reaction.getEmoji());

        holder.count.setText(String.valueOf(reaction.getCount()));

        holder.itemView.setOnClickListener(view -> toggleChecked(holder.getAdapterPosition()));
        holder.itemView.setBackgroundResource(0);

        if (userIdsWhoLiked != null && userIdsWhoLiked.contains(userId))
        {
            reaction.setChecked(true);
        }


        if (reaction.isChecked())
        {
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.background_item_reaction_selected);
        }
    }

    public boolean addReaction(String emoji)
    {
        for (int i = 0; i < reactions.size(); i++)
        {
            String currentEmoji = reactions.get(i)
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
        reactions.add(new Reaction(emoji, usersWhoLiked,true));
        if (ownerIsPost)
            MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
        else
            MainActivity.MOB_SERVER_API.commentReact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        notifyItemInserted(reactions.size() - 1);
        return true;
    }

    private void toggleChecked(int position)
    {
        // half-measure

        Reaction reaction = reactions.get(position);

        int userId = mainActivity.getPrivatePreferences()
                                 .getInt(MainActivity.USER_ID_KEY, -1);
        List<Integer> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        String emoji = reaction.getEmoji();
        if (reaction.isChecked())
        {
            //TODO change
            reaction.setChecked(false);
            userIdsWhoLiked.remove((Object) userId);
            if (ownerIsPost)
                MainActivity.MOB_SERVER_API.postUnreact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
            else
                MainActivity.MOB_SERVER_API.commentUnreact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        }
        else
        {
            //TODO change
            reaction.setChecked(true);
            userIdsWhoLiked.add(userId);
            if (ownerIsPost)
                MainActivity.MOB_SERVER_API.postReact(new PostChangeReactCallback(), postId, emoji, MainActivity.token);
            else
                MainActivity.MOB_SERVER_API.commentReact(new CommentChangeReactCallback(), postId, commentId, emoji, MainActivity.token);
        }

        //TODO will be remade
        if (reaction.getCount() == 0)
        {
            reactions.remove(position);
            notifyItemRemoved(position);
        }
        else
        {
            for (int i = 0; i < reactions.size(); i++)
            {
                Reaction currentReaction = reactions.get(i);
                if (reaction.getCount() >= currentReaction.getCount())
                {
                    reactions.remove(position);
                    notifyItemRemoved(position);
                    reactions.add(i, reaction);
                    notifyItemInserted(i);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return reactions.size();
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
}
