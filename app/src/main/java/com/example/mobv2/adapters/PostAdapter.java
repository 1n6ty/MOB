package com.example.mobv2.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostItem>
{


    @NonNull
    @Override
    public PostItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostItem holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    protected class PostItem extends RecyclerView.ViewHolder
    {

        public PostItem(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
