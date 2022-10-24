package com.example.mobv2.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.Addable;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.abstractions.HavingCommentsIds;
import com.example.mobv2.models.abstractions.User;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import localdatabase.daos.UserDao;
import serverapi.MOBServerAPI;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
        implements Addable<CommentImpl>
{
    private final UserDao userDao;

    private final MainActivity mainActivity;
    private final HavingCommentsIds havingCommentsIds;

    private final List<CommentImpl> comments;

    private int lastIndex = 0;

    public CommentsAdapter(MainActivity mainActivity,
                           HavingCommentsIds havingCommentsIds)
    {
        this.mainActivity = mainActivity;
        this.havingCommentsIds = havingCommentsIds;
        this.comments = new ArrayList<>();

        userDao = mainActivity.appDatabase.userDao();

        var commentsIds = havingCommentsIds.getCommentsIds();
        for (int i = 0; i < Math.min(commentsIds.size(), 4); i++)
        {
            mainActivity.mobServerAPI.getComment(new MOBServerAPI.MOBAPICallback()
            {
                @Override
                public void funcOk(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());

                    var response = (LinkedTreeMap<String, Object>) obj.get("response");

                    CommentImpl comment = new CommentImpl.CommentBuilder().parseFromMap(response);
                    addElement(comment);

                    lastIndex++;
                }

                @Override
                public void funcBad(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());
                }

                @Override
                public void fail(Throwable obj)
                {
                    Log.v("DEBUG", obj.toString());
                }
            }, commentsIds.get(commentsIds.size() - 1 - i), MainActivity.token);
        }
    }

    /*@Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView,
                                             int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx,
                                   int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                final int totalItemCount = layoutManager.getItemCount();
                final int visibleItemCount = layoutManager.getChildCount();
                final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (lastIndex < getItemCount() && (totalItemCount - visibleItemCount <= firstVisibleItem))
                {
                    var commentsIds = havingCommentsIds.getCommentsIds();
                    mainActivity.mobServerAPI.getComment(new MOBServerAPI.MOBAPICallback()
                    {
                        @Override
                        public void funcOk(LinkedTreeMap<String, Object> obj)
                        {
                            Log.v("DEBUG", obj.toString());

                            var response = (LinkedTreeMap<String, Object>) obj.get("response");

                            CommentImpl comment =
                                    new CommentImpl.CommentBuilder().parseFromMap(response);
                            addElement(comment);
                        }

                        @Override
                        public void funcBad(LinkedTreeMap<String, Object> obj)
                        {
                            Log.v("DEBUG", obj.toString());
                        }

                        @Override
                        public void fail(Throwable obj)
                        {
                            Log.v("DEBUG", obj.toString());
                        }
                    }, commentsIds.get(lastIndex++), MainActivity.token);
                }
            }
        });
    }*/

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType)
    {
        View commentItem = LayoutInflater.from(parent.getContext())
                                         .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(commentItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder,
                                 int position)
    {
        CommentImpl comment = comments.get(position);
        User user = comment.getUser();

        MainActivity.loadImageInView(user.getAvatarUrl(), holder.itemView, holder.avatarView);

        holder.fullNameView.setText(user.getFullName());

        holder.commentTextView.setText(comment.getText());

        holder.dateView.setText(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(comment.getDate()));

        holder.itemView.setOnClickListener(view -> onItemViewClick(view, holder.getAdapterPosition()));

        holder.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(holder.reactionsRecyclerView));

        var reactionsAdapter =
                new ReactionsCommentAdapter(mainActivity, comment.getReactions(), comment.getId());
        holder.reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        holder.reactionsRecyclerView.setAdapter(reactionsAdapter);

        holder.showCommentsButton.setVisibility(comment.getCommentsCount()
                                                       .get() > 0 ? View.VISIBLE : View.GONE);
        holder.showCommentsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addElement(@NonNull CommentImpl comment)
    {
        Date date = comment.getDate();
        if (comments.isEmpty() || date.compareTo(comments.get(comments.size() - 1)
                                                         .getDate()) < 0)
        {
            comments.add(comment);
            notifyItemInserted(comments.size() - 1);
        }
        else
        {
            for (int i = 0; i < comments.size(); i++)
            {
                Date currentDate = comments.get(i)
                                           .getDate();
                if (date.compareTo(currentDate) >= 0)
                {
                    comments.add(i, comment);
                    notifyItemInserted(i);
                    break;
                }
            }
        }
    }

    private void onItemViewClick(View view,
                                 int position)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_post);

        initMenu(view, popupMenu, position);
        popupMenu.show();
    }

    private void initMenu(View view,
                          @NonNull PopupMenu popupMenu,
                          int position)
    {
        var menu = popupMenu.getMenu();
        var comment = comments.get(position);
        var user = comment.getUser();

        boolean isCreator = user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
        menu.findItem(R.id.menu_edit_post)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete_post)
            .setVisible(isCreator);

        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, position));

        int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var reactionsView = (RecyclerView) view.findViewById(R.id.reactions_recycler_view);
        var reactionsAdapter = (ReactionsCommentAdapter) reactionsView.getAdapter();
        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item ->
                {
                    reactionsAdapter.addElement(item.getTitle()
                                                    .toString());
                    return true;
                });
        }
    }

    private boolean onMenuItemClick(MenuItem item,
                                    int position)
    {
        switch (item.getItemId())
        {
            case R.id.menu_copy_post:
                return copyComment(position);
            case R.id.menu_forward_post:
                return forwardComment(position);
            case R.id.menu_edit_post:
                return editComment(position);
            case R.id.menu_delete_post:
                return deleteComment(position);
            default:
                return false;
        }
    }

    private boolean copyComment(int position)
    {
        var comment = comments.get(position);

        var clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText("simple text", comment.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean forwardComment(int position)
    {
        Toast.makeText(mainActivity, "Forwarded", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean editComment(int position)
    {
        Toast.makeText(mainActivity, "Edited", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private boolean deleteComment(int position)
    {
        var comment = comments.get(position);

        comments.remove(position);
        notifyItemRemoved(position);

        havingCommentsIds.getCommentsIds()
                         .remove(comment.getId());

        mainActivity.mobServerAPI.commentDelete(new MOBAPICallbackImpl(), comment.getId(), MainActivity.token);

        Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_LONG)
             .show();

        return true;
    }

    @Override
    public int getItemCount()
    {
        return comments.size();
    }

    protected static class CommentViewHolder extends RecyclerView.ViewHolder
    {
        private final ShapeableImageView avatarView;
        private final TextView fullNameView;
        private final TextView commentTextView;
        private final TextView dateView;
        private final ImageView appreciationUpView;
        private final TextView ratesCountView;
        private final ImageView appreciationDownView;
        private final ImageView showReactionsView;
        private final RecyclerView reactionsRecyclerView;
        private final Button showCommentsButton;

        public CommentViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemCommentBinding binding = ItemCommentBinding.bind(itemView);

            avatarView = binding.avatarView;
            fullNameView = binding.fullNameView;
            commentTextView = binding.commentTextView;
            dateView = binding.commentDateView;
            appreciationUpView = binding.appreciationUpView;
            ratesCountView = binding.ratesCountView;
            appreciationDownView = binding.appreciationDownView;
            showReactionsView = binding.showReactionsView;
            reactionsRecyclerView = binding.reactionsRecyclerView;
            showCommentsButton = binding.showCommentsButton;
        }
    }
}
