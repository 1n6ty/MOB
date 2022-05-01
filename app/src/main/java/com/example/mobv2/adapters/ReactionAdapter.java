package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemReactionBinding;
import com.example.mobv2.models.Reaction;

import java.util.ArrayList;
import java.util.List;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder>
{
    private List<ReactionItem> reactionItems;

    public ReactionAdapter(List<Reaction> reactions)
    {
        reactionItems = new ArrayList<>();

        for (Reaction reaction : reactions)
        {
            reactionItems.add(new ReactionItem(reaction));
        }
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType)
    {
        View reactionItem = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_reaction, parent, false);
        return new ReactionViewHolder(reactionItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder,
                                 int position)
    {
        ReactionItem item = reactionItems.get(position);

        holder.reaction.setText(item.getReaction()
                                    .getEmoji());
        holder.count.setText(String.valueOf(item.getReaction()
                                                .getCount()));

        holder.itemView.setOnClickListener(v -> toggleChecked(position));

        holder.itemView.setBackgroundResource(R.drawable.background_item_reaction_selector);

        if (item.isChecked())
        {
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.background_item_reaction_selected);
        }
    }

    private void toggleChecked(int position)
    {
        // half-measure

        ReactionItem item = reactionItems.get(position);

        if (item.isChecked())
        {
            item.setChecked(false);
            item.getReaction()
                .setCount(item.getReaction()
                              .getCount() - 1);
        }
        else
        {
            item.setChecked(true);
            item.getReaction()
                .setCount(item.getReaction()
                              .getCount() + 1);
        }

        notifyItemChanged(position);
    }

    @Override
    public int getItemCount()
    {
        return reactionItems.size();
    }

    protected class ReactionViewHolder extends RecyclerView.ViewHolder
    {
        private TextView reaction;
        private TextView count;

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
        private Reaction reaction;
        private boolean checked;

        public ReactionItem(Reaction reaction)
        {
            this.reaction = reaction;
            this.checked = false;
        }

        public Reaction getReaction()
        {
            return reaction;
        }

        public boolean isChecked()
        {
            return checked;
        }

        public void setChecked(boolean checked)
        {
            this.checked = checked;
        }
    }
}
