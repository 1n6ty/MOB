package com.example.mobv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.abstraction.AbleToAdd;
import com.example.mobv2.databinding.ItemAddressBinding;
import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.item.AddressItem;
import com.example.mobv2.util.MyObservableArrayList;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder> implements AbleToAdd<AddressImpl>
{
    private final MainActivity mainActivity;
    private final MyObservableArrayList<AddressItem> addressItemList;

    public AddressesAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.addressItemList = new MyObservableArrayList<>();
        addressItemList.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(int index,
                                AddressItem element)
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
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType)
    {
        View addressItem = LayoutInflater.from(parent.getContext())
                                         .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(addressItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder,
                                 int position)
    {
        var address = addressItemList.get(position);

        address.refreshItemBinding(holder.binding);
    }

    @Override
    public void addElement(@NonNull AddressImpl address)
    {
        try
        {
            addressItemList.add(new AddressItem(mainActivity, this, address));
            notifyItemInserted(addressItemList.size() - 1);
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    public boolean checkIfAddressEqualsClickedAddress(AddressItem addressItem)
    {
        return addressItem.addressItemHelper.compareById(getClickedAddressItem().addressItemHelper);
    }

    public void deselectClickedAddress()
    {
        var clickedAddress = getClickedAddressItem();
        clickedAddress.addressItemHelper.setCurrent(false);
        mainActivity.appDatabase.addressDao()
                                .update(clickedAddress.addressItemHelper.getAddress());

        for (int i = 0; i < addressItemList.size(); i++)
        {
            var addressItem = addressItemList.get(i);
            if (addressItem.addressItemHelper.compareById(clickedAddress.addressItemHelper))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private AddressItem getClickedAddressItem()
    {
        for (int i = 0; i < addressItemList.size(); i++)
        {
            var addressItem = addressItemList.get(i);
            if (addressItem.addressItemHelper.isCurrent()) return addressItem;
        }

        return new AddressItem(mainActivity, this, new AddressImpl());
    }

    public void notifyItemChanged(AddressItem addressItem)
    {
        notifyItemChanged(addressItemList.indexOf(addressItem));
    }

    @Override
    public int getItemCount()
    {
        return addressItemList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemAddressBinding binding;

        public AddressViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = ItemAddressBinding.bind(itemView);
        }
    }
}
