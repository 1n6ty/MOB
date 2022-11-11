package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.abstractions.AbleToAdd;
import com.example.mobv2.databinding.ItemAddressBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.views.items.AddressItemHelper;

import java.util.ArrayList;
import java.util.List;

import localdatabase.daos.AddressDao;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder> implements AbleToAdd<AddressImpl>
{
    private final AddressDao addressDao;

    private final MainActivity mainActivity;
    private final List<AddressItemHelper> addressItemHelperList;

    public AddressesAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.addressItemHelperList = new ArrayList<>();

        addressDao = mainActivity.appDatabase.addressDao();
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
        var address = addressItemHelperList.get(position);

        address.refreshItemBinding(holder.binding);
    }

    public boolean checkIfAddressEqualsClickedAddress(AddressItemHelper addressItemHelper)
    {
        return addressItemHelper.addressItemHelper.compareById(getClickedAddressItem());
    }

    public void deselectClickedAddress()
    {
        var clickedAddress = getClickedAddressItem();
        clickedAddress.addressItemHelper.setCurrent(false);
        addressDao.update(clickedAddress.addressItemHelper.getAddress());

        for (int i = 0; i < addressItemHelperList.size(); i++)
        {
            var addressItem = addressItemHelperList.get(i);
            if (addressItem.addressItemHelper.compareById(clickedAddress))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private AddressItemHelper getClickedAddressItem()
    {
        for (int i = 0; i < addressItemHelperList.size(); i++)
        {
            var addressItem = addressItemHelperList.get(i);
            if (addressItem.addressItemHelper.isCurrent())
                return addressItem;
        }

        return new AddressItemHelper(mainActivity, this, new AddressImpl());
    }

    @Override
    public void addElement(@NonNull AddressImpl address)
    {
        try
        {
            addressItemHelperList.add(new AddressItemHelper(mainActivity, this, address));
            notifyItemInserted(addressItemHelperList.size() - 1);
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return addressItemHelperList.size();
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
