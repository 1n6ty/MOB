package com.example.mobv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.abstraction.AbleToAdd;
import com.example.mobv2.adapter.abstraction.AbleToReverse;
import com.example.mobv2.adapter.abstraction.AbleToSortByUserWills;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.model.PostImpl;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.item.PostItem;
import com.example.mobv2.util.MyObservableArrayList;

import java.util.Collections;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
        implements AbleToAdd<PostImpl>, AbleToReverse, AbleToSortByUserWills
{
    private final MainActivity mainActivity;

    private final MarkersAdapter markersAdapter;

    private final MyObservableArrayList<PostItem> postItemList;

    public PostsAdapter(MainActivity mainActivity,
                        MarkersAdapter markersAdapter)
    {
        this.mainActivity = mainActivity;
        this.markersAdapter = markersAdapter;

        postItemList = new MyObservableArrayList<>();
        postItemList.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(int index,
                                PostItem element)
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
            public void onClear(int count)
            {
                notifyItemRangeRemoved(0, count);
            }
        });
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType)
    {
        View postItem = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,
                                 int position)
    {
        var postItem = postItemList.get(position);

        postItem.refreshItemBinding(holder.binding);
    }

    @Override
    public void addElement(@NonNull PostImpl post)
    {
        addElementAndGetItem(post);
    }

    public PostItem addElementAndGetItem(@NonNull PostImpl post)
    {
        Date date = post.getDate();
        PostItem postItem = new PostItem(mainActivity, this, post);
        if (postItemList.isEmpty() || date.compareTo(postItemList.get(postItemList.size() - 1).postItemHelper.getDate()) <= 0)
        {
            postItemList.add(postItem);
            return postItem;
        }

        for (int i = 0; i < postItemList.size(); i++)
        {
            Date currentDate = postItemList.get(i).postItemHelper.getDate();
            if (date.compareTo(currentDate) > 0)
            {
                postItemList.add(i, postItem);
                return postItem;
            }
        }

        return null;
    }

    @Override
    public boolean reverse()
    {
        Collections.reverse(postItemList);
        notifyItemRangeChanged(0, postItemList.size());
        return true;
    }

    @Override
    public boolean sortByRates()
    {
        Collections.sort(postItemList, (postItem, nextPostItem) ->
        {
            var postPositiveRates = postItem.postItemHelper.getPositiveRates();
            var postNegativeRates = postItem.postItemHelper.getNegativeRates();
            var nextPostPositiveRates = nextPostItem.postItemHelper.getPositiveRates();
            var nextPostNegativeRates = nextPostItem.postItemHelper.getNegativeRates();

            return Integer.compare(nextPostPositiveRates.size() - nextPostNegativeRates.size(),
                    postPositiveRates.size() - postNegativeRates.size());
        });
        notifyItemRangeChanged(0, postItemList.size());
        return true;
    }

    @Override
    public boolean sortByDate()
    {
        Collections.sort(postItemList, (postItem, nextPostItem) ->
        {
            var nextPostItemHelperDate = nextPostItem.postItemHelper.getDate();
            var postItemHelperDate = postItem.postItemHelper.getDate();

            return (nextPostItemHelperDate.compareTo(postItemHelperDate));
        });
        notifyItemRangeChanged(0, postItemList.size());
        return true;
    }

    @Override
    public boolean sortByComments()
    {
        Collections.sort(postItemList, (postItem, nextPostItem) ->
        {
            var nextPostItemHelperCommentIds = nextPostItem.postItemHelper.getCommentIds();
            var postItemHelperCommentIds = postItem.postItemHelper.getCommentIds();

            return Integer.compare(nextPostItemHelperCommentIds.size(), postItemHelperCommentIds.size());
        });
        notifyItemRangeChanged(0, postItemList.size());
        return true;
    }

    public void deletePostItem(PostItem postItem)
    {
        postItemList.remove(postItem);
    }

    @Override
    public int getItemCount()
    {
        return postItemList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemPostBinding binding;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemPostBinding.bind(itemView);
        }
    }
}
