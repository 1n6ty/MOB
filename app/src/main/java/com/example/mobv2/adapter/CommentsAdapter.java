package com.example.mobv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.abstraction.AbleToAdd;
import com.example.mobv2.adapter.abstraction.AbleToReverse;
import com.example.mobv2.adapter.abstraction.AbleToSortByUserWills;
import com.example.mobv2.callback.GetCommentCallback;
import com.example.mobv2.callback.abstraction.GetCommentOkCallback;
import com.example.mobv2.databinding.ItemCommentBinding;
import com.example.mobv2.model.CommentImpl;
import com.example.mobv2.model.abstraction.HavingCommentsIds;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.view.item.CommentItem;
import com.example.mobv2.util.MyObservableArrayList;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Collections;

import localDatabase.dao.CommentDao;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>
        implements AbleToAdd<CommentImpl>, AbleToReverse, AbleToSortByUserWills, NestedScrollView.OnScrollChangeListener, GetCommentOkCallback
{
    private final int maxUploadedCommentsCount = 6;
    private final CommentDao commentDao;

    private final MainActivity mainActivity;
    private final NestedScrollView nestedScrollView;

    private final HavingCommentsIds havingCommentsIds;
    private final MyObservableArrayList<CommentItem> commentItemList;

    public CommentsAdapter(MainActivity mainActivity,
                           NestedScrollView nestedScrollView,
                           HavingCommentsIds havingCommentsIds)
    {
        this.mainActivity = mainActivity;
        this.nestedScrollView = nestedScrollView;
        this.havingCommentsIds = havingCommentsIds;
        this.commentItemList = new MyObservableArrayList<>();
        commentItemList.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<CommentItem>()
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
            public void onRemoved(int index,
                                  Object o)
            {
                notifyItemRemoved(index);
            }

            @Override
            public void onClear()
            {
                notifyItemRangeRemoved(0, getItemCount());
            }
        });

        commentDao = mainActivity.appDatabase.commentDao();

        var commentsIds = havingCommentsIds.getCommentIds();
        for (int i = 0; i < Math.min(commentsIds.size(), maxUploadedCommentsCount); i++)
        {
            getCommentByIndex(commentsIds.size() - 1 - i);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        nestedScrollView.setOnScrollChangeListener(this::onScrollChange);
    }

    @Override
    public void onScrollChange(NestedScrollView view,
                               int scrollX,
                               int scrollY,
                               int oldScrollX,
                               int oldScrollY)
    {
        var commentsIds = havingCommentsIds.getCommentIds();
        if (commentsIds.size() - 1 >= getItemCount())
        {
            View childView = view.getChildAt(view.getChildCount() - 1);

            if (scrollY > oldScrollY && scrollY >= childView.getMeasuredHeight() - view.getMeasuredHeight())
            {
                getCommentByIndex(commentsIds.size() - 1 - getItemCount());
            }
        }
    }

    private void getCommentByIndex(int index)
    {
        var commentsIds = havingCommentsIds.getCommentIds();
        String commentId = commentsIds.get(index);
        var callback = new GetCommentCallback(mainActivity);
        callback.setOkCallback(this::parseCommentFromMapAndAddToComments);
        callback.setFailCallback(() -> getCommentByIdFromLocalDbAndAddToComments(commentId));

        mainActivity.mobServerAPI.getComment(callback, commentId, MainActivity.token);
    }

    @Override
    public void parseCommentFromMapAndAddToComments(LinkedTreeMap<String, Object> map)
    {
        var comment = new CommentImpl.CommentBuilder().parseFromMap(map);
        commentDao.insert(comment);
        addElement(comment);
    }

    private void getCommentByIdFromLocalDbAndAddToComments(String commentId)
    {
        var comment = commentDao.getById(commentId);
        if (comment == null)
        {
            Toast.makeText(mainActivity, "Comment is not uploaded", Toast.LENGTH_LONG)
                 .show();
            return;
        }

        addElement(comment);
    }

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
        var commentItem = commentItemList.get(position);

        commentItem.refreshItemBinding(holder.binding);
        commentItem.commentItemHelper.setHavingCommentsIds(havingCommentsIds);
    }

    @Override
    public void addElement(@NonNull CommentImpl comment)
    {
        addElementAndGet(comment);
    }

    public CommentItem addElementAndGet(@NonNull CommentImpl comment)
    {
        var date = comment.getDate();
        var commentItem = new CommentItem(mainActivity, this, comment);
        if (commentItemList.isEmpty() || date.compareTo(commentItemList.get(commentItemList.size() - 1).commentItemHelper.getDate()) <= 0)
        {
            commentItemList.add(commentItem);
            return commentItem;
        }

        for (int i = 0; i < commentItemList.size(); i++)
        {
            var currentDate = commentItemList.get(i).commentItemHelper.getDate();
            if (date.compareTo(currentDate) > 0)
            {
                commentItemList.add(i, commentItem);
                return commentItem;
            }
        }

        return null;
    }

    @Override
    public boolean reverse()
    {
        Collections.reverse(commentItemList);
        notifyItemRangeChanged(0, commentItemList.size());
        return true;
    }

    @Override
    public boolean sortByAppreciations()
    {
        Collections.sort(commentItemList, (commentItem, nextCommentItem) ->
        {
            var commentPositiveRates = commentItem.commentItemHelper.getPositiveRates();
            var commentNegativeRates = commentItem.commentItemHelper.getNegativeRates();
            var nextCommentPositiveRates = nextCommentItem.commentItemHelper.getPositiveRates();
            var nextCommentNegativeRates = nextCommentItem.commentItemHelper.getNegativeRates();

            return Integer.compare(nextCommentPositiveRates.size() - nextCommentNegativeRates.size(),
                    commentPositiveRates.size() - commentNegativeRates.size());
        });
        notifyItemRangeChanged(0, commentItemList.size());
        return true;
    }

    @Override
    public boolean sortByDate()
    {
        Collections.sort(commentItemList, (commentItem, nextCommentItem) ->
        {
            var nextPostItemHelperDate = nextCommentItem.commentItemHelper.getDate();
            var postItemHelperDate = commentItem.commentItemHelper.getDate();

            return (nextPostItemHelperDate.compareTo(postItemHelperDate));
        });
        notifyItemRangeChanged(0, commentItemList.size());
        return true;
    }

    @Override
    public boolean sortByComments()
    {
        Collections.sort(commentItemList, (commentItem, nextCommentItem) ->
        {
            var nextPostItemHelperCommentIds = nextCommentItem.commentItemHelper.getCommentIds();
            var postItemHelperCommentIds = commentItem.commentItemHelper.getCommentIds();

            return Integer.compare(nextPostItemHelperCommentIds.size(), postItemHelperCommentIds.size());
        });
        notifyItemRangeChanged(0, commentItemList.size());
        return true;
    }

    public void scrollTo(int x, int y)
    {
        nestedScrollView.scrollTo(x, y);
    }

    public void deleteComment(CommentItem commentItem)
    {
        commentItemList.remove(commentItem);
    }

    @Override
    public int getItemCount()
    {
        return commentItemList.size();
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
