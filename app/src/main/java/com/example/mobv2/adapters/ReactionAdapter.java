package com.example.mobv2.adapters;

import android.content.Context;
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
import com.example.mobv2.utils.abstractions.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder>
{
    private final Context context;
    private List<ReactionItem> reactionItems;

    public ReactionAdapter(Context context,
                           List<Reaction> reactions)
    {
        this.context = context;
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
        // TODO: FIX THIS SHIT CODE

        ReactionItem item = reactionItems.get(position);

        final boolean isAddButton = item.getReaction()
                                        .isAdd();

        holder.reaction.setText(item.getReaction()
                                    .getEmoji());

        holder.count.setText(String.valueOf(item.getReaction()
                                                .getCount()));

        if (isAddButton)
        {
            holder.count.setVisibility(View.GONE);
        }
        else
        {
            holder.count.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(view -> onReactionItemClicked(view, isAddButton, position));

        holder.itemView.setBackgroundResource(R.drawable.background_item_reaction_selector);

        if (item.isChecked())
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
                    new ContextThemeWrapper(context, R.style.MyPopupOtherStyle);
            PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, view);
            popupMenu.inflate(R.menu.menu_reactions);

            initMenu(popupMenu);
            // inflate menu
            popupMenu.show();
        }
        else
        {
            toggleChecked(position);
        }
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        Menu menu = popupMenu.getMenu();

        HashMap<Integer, Operation<Integer, Boolean>> popupMenuCommands =
                new HashMap<Integer, Operation<Integer, Boolean>>()
                {
                    {
                        put(R.id.menu_reaction_like, ReactionAdapter.this::placeReaction);
                        put(R.id.menu_reaction_dislike, ReactionAdapter.this::placeReaction);
                        put(R.id.menu_reaction_love, ReactionAdapter.this::placeReaction);
                    }
                };

        for (Integer id : popupMenuCommands.keySet())
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item -> Objects.requireNonNull(popupMenuCommands.get(id))
                                                           .execute(id));
        }
    }

    private boolean placeReaction(int id)
    {
        HashMap<Integer, String> ids = new HashMap<Integer, String>()
        {
            {
                put(R.id.menu_reaction_like, Reaction.EMOJI_LIKE);
                put(R.id.menu_reaction_dislike, Reaction.EMOJI_DISLIKE);
                put(R.id.menu_reaction_love, Reaction.EMOJI_LOVE);
            }
        };

        reactionItems.add(reactionItems.size() - 1, new ReactionItem(new Reaction(ids.get(id), 1), true));
        notifyItemInserted(reactionItems.size() - 2);
        return true;
    }

    private void toggleChecked(int position)
    {
        // half-measure

        ReactionItem item = reactionItems.get(position);
        Reaction reaction = item.getReaction();

        if (item.isChecked())
        {
            item.setChecked(false);
            reaction.setCount(reaction.getCount() - 1);
        }
        else
        {
            item.setChecked(true);
            reaction.setCount(reaction.getCount() + 1);
        }

        if (reaction.getCount() == 0)
        {
            reactionItems.remove(position);
            notifyItemRemoved(position);
            return;
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
