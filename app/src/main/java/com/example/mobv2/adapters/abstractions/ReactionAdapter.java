package com.example.mobv2.adapters.abstractions;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.databinding.ItemReactionBinding;

public interface ReactionAdapter
{
    boolean addReaction(String emoji);

    class ReactionViewHolder extends RecyclerView.ViewHolder
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

        public TextView getReaction()
        {
            return reaction;
        }

        public TextView getCount()
        {
            return count;
        }
    }
}
