package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.AbleToAdd;
import com.example.mobv2.adapters.abstractions.AbleToReverse;
import com.example.mobv2.adapters.abstractions.AbleToSortByUserWills;
import com.example.mobv2.databinding.ItemPostBinding;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.example.mobv2.ui.views.PostItem;
import com.example.mobv2.utils.MyObservableArrayList;

import java.util.Collections;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>
        implements AbleToReverse, AbleToSortByUserWills, AbleToAdd<PostImpl>
{
    private final MainActivity mainActivity;

    private final MarkersAdapter markersAdapter;

    private final MyObservableArrayList<PostItem> postItems;

    public PostsAdapter(MainActivity mainActivity,
                        MarkersAdapter markersAdapter)
    {
        this.mainActivity = mainActivity;
        this.markersAdapter = markersAdapter;

        postItems = new MyObservableArrayList<>();
        postItems.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
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
            public void onRemoved(int index, Object o)
            {
                notifyItemRemoved(index);
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
    public void onViewAttachedToWindow(@NonNull PostViewHolder holder)
    {
        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);

        var position = holder.getAdapterPosition();

        if (position > -1)
        {
            var postItem = postItems.get(position);

            mainFragmentViewModel.setPostTitle(postItem.postItemHelper.getTitle());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,
                                 int position)
    {
        var postItem = postItems.get(position);

        postItem.refreshItemBinding(holder.binding);
    }

    @Override
    public void addElement(@NonNull PostImpl post)
    {
        Date date = post.getDate();
        if (postItems.isEmpty() || date.compareTo(postItems.get(postItems.size() - 1).postItemHelper.getDate()) < 0)
        {
            PostItem postItem = new PostItem(mainActivity,  markersAdapter,this, post);
            postItems.add(postItem);
            return;
        }

        for (int i = 0; i < postItems.size(); i++)
        {
            Date currentDate = postItems.get(i).postItemHelper.getDate();
            if (date.compareTo(currentDate) >= 0)
            {
                PostItem postItem = new PostItem(mainActivity, markersAdapter, this, post);
                postItems.add(i, postItem);
                break;
            }
        }
    }

    public PostItem addElementAndGetItem(@NonNull PostImpl post)
    {
        Date date = post.getDate();
        if (postItems.isEmpty() || date.compareTo(postItems.get(postItems.size() - 1).postItemHelper.getDate()) < 0)
        {
            PostItem postItem = new PostItem(mainActivity,  markersAdapter,this, post);
            postItems.add(postItem);
            return postItem;
        }

        for (int i = 0; i < postItems.size(); i++)
        {
            Date currentDate = postItems.get(i).postItemHelper.getDate();
            if (date.compareTo(currentDate) >= 0)
            {
                PostItem postItem = new PostItem(mainActivity, markersAdapter, this, post);
                postItems.add(i, postItem);
                return postItem;
            }
        }

        return null;
    }


    @Override
    public boolean reverse()
    {
        Collections.reverse(postItems);
        notifyItemRangeChanged(0, postItems.size());
        return true;
    }

    @Override
    public boolean sortByAppreciations()
    {
        Collections.sort(postItems, (postItem, nextPostItem) ->
        {
            var postPositiveRates = postItem.postItemHelper.getPositiveRates();
            var nextPostPositiveRates = nextPostItem.postItemHelper.getPositiveRates();

            return Integer.compare(nextPostPositiveRates.size(), postPositiveRates.size());
        });
        notifyItemRangeChanged(0, postItems.size());
        return true;
    }

    @Override
    public boolean sortByDate()
    {
        Collections.sort(postItems, (postItem, nextPostItem) ->
        {
            var nextPostItemHelperDate = nextPostItem.postItemHelper.getDate();
            var postItemHelperDate = postItem.postItemHelper.getDate();

            return (nextPostItemHelperDate.compareTo(postItemHelperDate));
        });
        notifyItemRangeChanged(0, postItems.size());
        return true;
    }

    @Override
    public boolean sortByComments()
    {
        Collections.sort(postItems, (postItem, nextPostItem) ->
        {
            var nextPostItemHelperCommentIds = nextPostItem.postItemHelper.getCommentIds();
            var postItemHelperCommentIds = postItem.postItemHelper.getCommentIds();

            return Integer.compare(nextPostItemHelperCommentIds.size(), postItemHelperCommentIds.size());
        });
        notifyItemRangeChanged(0, postItems.size());
        return true;
    }

    public void deletePostItem(PostItem postItem)
    {
        postItems.remove(postItem);
    }

    public void clear()
    {
        int size = getItemCount();
        postItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount()
    {
        return postItems.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        private final com.example.mobv2.databinding.ItemPostBinding binding;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemPostBinding.bind(itemView);
        }
    }
}
