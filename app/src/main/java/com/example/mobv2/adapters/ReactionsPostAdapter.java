package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.ReactionsAdapter;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ReactionsPostAdapter extends RecyclerView.Adapter<ReactionsPostAdapter.ReactionViewHolder>
        implements ReactionsAdapter
{
    private final MainActivity mainActivity;
    private final List<Reaction> reactions;
    private final String postId;

    public ReactionsPostAdapter(MainActivity mainActivity,
                                List<Reaction> reactions,
                                String postId)
    {
        this.mainActivity = mainActivity;
        this.reactions = reactions;
        this.postId = postId;
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
        List<String> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        String userId = mainActivity.getPrivatePreferences()
                                    .getString(MainActivity.USER_ID_KEY, "");

        holder.getReaction()
              .setText(reaction.getEmoji());

        holder.getCount()
              .setText(String.valueOf(reaction.getCount()));

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

        List<String> userIdsWhoLiked = new ArrayList<>();
        String userId = mainActivity.getPrivatePreferences()
                                    .getString(MainActivity.USER_ID_KEY, "");
        userIdsWhoLiked.add(userId);

        //TODO change
        reactions.add(new Reaction(emoji, userIdsWhoLiked, true));
        MainActivity.MOB_SERVER_API.postReact(new MOBAPICallbackImpl(), postId, emoji, MainActivity.token);
        notifyItemInserted(reactions.size() - 1);
        return true;
    }

    private void toggleChecked(int position)
    {
        // half-measure

        Reaction reaction = reactions.get(position);

        String userId = mainActivity.getPrivatePreferences()
                                    .getString(MainActivity.USER_ID_KEY, "");
        List<String> userIdsWhoLiked = reaction.getUserIdsWhoLiked();
        String emoji = reaction.getEmoji();
        if (reaction.isChecked())
        {
            reaction.setChecked(false);
            userIdsWhoLiked.remove(userId);
            MainActivity.MOB_SERVER_API.postUnreact(new MOBAPICallbackImpl(), postId, emoji, MainActivity.token);
        }
        else
        {
            reaction.setChecked(true);
            userIdsWhoLiked.add(userId);
            MainActivity.MOB_SERVER_API.postReact(new MOBAPICallbackImpl(), postId, emoji, MainActivity.token);
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
                    reactions.set(i, reaction);
                    notifyItemChanged(i);
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
}
