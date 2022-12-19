package com.example.mobv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.abstraction.AbleToAdd;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.model.Reaction;
import com.example.mobv2.model.abstraction.UserContent;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.item.ReactionItem;
import com.example.mobv2.util.MyObservableArrayList;

import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>
        implements AbleToAdd<Reaction>
{
    private final MainActivity mainActivity;
    private final MyObservableArrayList<ReactionItem> reactionItemList;
    private final UserContent userContent;

    public ReactionsAdapter(MainActivity mainActivity,
                            List<Reaction> reactions,
                            UserContent userContent)
    {
        this.mainActivity = mainActivity;
        this.reactionItemList = new MyObservableArrayList<>();
        this.reactionItemList.setOnListChangedCallback(
                new MyObservableArrayList.OnListChangedCallback<>()
                {
                    @Override
                    public void onAdded(int index,
                                        ReactionItem element)
                    {
                        notifyItemInserted(index);
                    }

                    @Override
                    public void onRemoved(int index)
                    {
                        notifyItemRemoved(index);
                    }

                    @Override
                    public void onRemoved(int index,
                                          Object o)
                    {
                        notifyItemRemoved(index);
                    }
                });

        for (Reaction reaction : reactions)
        {
            ReactionItem reactionItem = new ReactionItem(mainActivity, this, reaction);
            reactionItem.reactionItemHelper.setUserContent(userContent);
            reactionItemList.add(reactionItem);
        }

        this.userContent = userContent;
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
        var reactionItem = reactionItemList.get(holder.getAdapterPosition());

        reactionItem.refreshItemBinding(holder.binding);
        reactionItem.reactionItemHelper.setUserContent(userContent);
    }

    @Override
    public void addElement(@NonNull Reaction reaction)
    {
        if (checkIfReactionWasAdded(reaction))
        {
            return;
        }

        var reactionItem = new ReactionItem(mainActivity, this, reaction);
        reactionItem.reactionItemHelper.setUserContent(userContent);
        int count = reaction.getCount().get();
        if (reactionItemList.isEmpty() || count < reactionItemList.get(
                reactionItemList.size() - 1).reactionItemHelper.getCount().get())
        {
            upAndAddReactionItemToReactionItemList(reactionItem);
            return;
        }

        for (int i = 0; i < reactionItemList.size(); i++)
        {
            int reactionItemCount = reactionItemList.get(i).reactionItemHelper.getCount().get();
            if (count >= reactionItemCount)
            {
                upAndAddReactionItemToReactionItemList(reactionItem);
                break;
            }
        }
    }

    private boolean checkIfReactionWasAdded(@NonNull Reaction reaction)
    {
        String emoji = reaction.getEmoji();

        for (int i = 0; i < reactionItemList.size(); i++)
        {
            var reactionItem = reactionItemList.get(i);
            String reactionItemEmoji = reactionItem.reactionItemHelper.getEmoji();
            if (emoji.equals(reactionItemEmoji))
            {
                reactionItem.toggleChecked();
                return true;
            }
        }
        return false;
    }

    private void upAndAddReactionItemToReactionItemList(ReactionItem reactionItem)
    {
        String userId = mainActivity.appDatabase.userDao().getCurrentId();
        reactionItem.reactionItemHelper.up(userId);
        reactionItemList.add(reactionItem);
    }

    public void deleteReactionItem(ReactionItem reactionItem)
    {
        reactionItemList.remove(reactionItem);
    }

    public void notifyItemChanged(ReactionItem reactionItem)
    {
        notifyItemChanged(reactionItemList.indexOf(reactionItem));
    }

    @Override
    public int getItemCount()
    {
        return reactionItemList.size();
    }

    public static class ReactionViewHolder extends RecyclerView.ViewHolder
    {
        public ItemReactionBinding binding;

        public ReactionViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemReactionBinding.bind(itemView);
        }
    }
}
