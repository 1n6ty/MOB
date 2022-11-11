package com.example.mobv2.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.AbleToAdd;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.abstractions.HavingCommentsIds;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.views.items.CommentItem;
import com.example.mobv2.utils.MyObservableArrayList;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Date;

import serverapi.MOBServerAPI;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
        implements AbleToAdd<CommentImpl>
{
    private final MainActivity mainActivity;

    private final HavingCommentsIds havingCommentsIds;
    private final MyObservableArrayList<CommentItem> commentItems;

    private int lastIndex = 0;

    public CommentsAdapter(MainActivity mainActivity,
                           HavingCommentsIds havingCommentsIds)
    {
        this.mainActivity = mainActivity;
        this.havingCommentsIds = havingCommentsIds;
        this.commentItems = new MyObservableArrayList<>();
        commentItems.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<CommentItem>()
        {
            @Override
            public void onAdded(int index,
                                CommentItem element)
            {
                notifyItemInserted(index);
            }

            @Override
            public void onRemoved(int index)
            {
                notifyItemRemoved(index);
            }

            @Override
            public void onRemoved(int index, Object o)
            {
                notifyItemRemoved(index);
            }

            @Override
            public void onClear()
            {
                notifyItemRangeRemoved(0, getItemCount());
            }
        });

        var commentsIds = this.havingCommentsIds.getCommentIds();
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
        var commentItem = commentItems.get(position);

        commentItem.refreshItemBinding(holder.binding);
        commentItem.commentItemHelper.setHavingCommentsIds(havingCommentsIds);
    }

    @Override
    public void addElement(@NonNull CommentImpl comment)
    {
        Date date = comment.getDate();
        CommentItem commentItem = new CommentItem(mainActivity, this, comment);
        if (commentItems.isEmpty() || date.compareTo(commentItems.get(commentItems.size() - 1).commentItemHelper.getDate()) < 0)
        {
            commentItems.add(commentItem);
            return;
        }

        for (int i = 0; i < commentItems.size(); i++)
        {
            Date currentDate = commentItems.get(i).commentItemHelper.getDate();
            if (date.compareTo(currentDate) >= 0)
            {
                commentItems.add(i, commentItem);
                break;
            }
        }
    }

    public CommentItem addElementAndGet(@NonNull CommentImpl comment)
    {
        Date date = comment.getDate();
        CommentItem commentItem = new CommentItem(mainActivity, this, comment);
        if (commentItems.isEmpty() || date.compareTo(commentItems.get(commentItems.size() - 1).commentItemHelper.getDate()) < 0)
        {
            commentItems.add(commentItem);
            return commentItem;
        }

        for (int i = 0; i < commentItems.size(); i++)
        {
            Date currentDate = commentItems.get(i).commentItemHelper.getDate();
            if (date.compareTo(currentDate) >= 0)
            {
                commentItems.add(i, commentItem);
                return commentItem;
            }
        }

        return null;
    }

    public void deleteComment(CommentItem commentItem)
    {
        commentItems.remove(commentItem);
    }

    @Override
    public int getItemCount()
    {
        return commentItems.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemCommentBinding binding;

        public CommentViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemCommentBinding.bind(itemView);
        }
    }
}
