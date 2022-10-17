package com.example.mobv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.SetAddressCallback;
import com.example.mobv2.databinding.ItemAddressBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

import localdatabase.daos.AddressDao;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder>
{
    private final AddressDao addressDao;

    private final MainActivity mainActivity;
    private final List<AddressImpl> addresses;

    public AddressesAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.addresses = new ArrayList<>();

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
        AddressImpl address = addresses.get(position);

        holder.addressPrimaryView.setText(address.getPrimary());
        holder.addressSecondaryView.setText(address.getSecondary());

        holder.itemView.setOnClickListener(view -> onAddressItemClick(position));

        holder.itemView.setBackgroundResource(R.drawable.background_item_address_selector);

        if (address.isCurrent())
        {
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.background_item_address_selected);
        }
    }

    private void onAddressItemClick(int position)
    {
        AddressImpl address = addresses.get(position);

        if (checkIfAddressEqualsClickedAddress(address))
            return;

        deselectClickedAddress();

        address.setCurrent(true);
        addressDao.update(address);

        mainActivity.mobServerAPI.setLocation(new SetAddressCallback(mainActivity), address.getId(), MainActivity.token);

        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
        mainFragmentViewModel.setAddressChanged(true);

        notifyItemChanged(position);
    }

    private void deselectClickedAddress()
    {
        AddressImpl clickedAddress = getClickedAddress();
        clickedAddress.setCurrent(false);
        addressDao.update(clickedAddress);

        for (int i = 0; i < addresses.size(); i++)
        {
            AddressImpl address = addresses.get(i);
            if (address.compareById(clickedAddress))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private AddressImpl getClickedAddress()
    {
        for (int i = 0; i < addresses.size(); i++)
        {
            AddressImpl address = addresses.get(i);
            if (address.isCurrent())
                return address;
        }

        return new AddressImpl();
    }

    private boolean checkIfAddressEqualsClickedAddress(AddressImpl address)
    {
        return address.compareById(getClickedAddress());
    }

    public void addAddress(AddressImpl address)
    {
        try
        {
            addresses.add(address);
            notifyItemInserted(addresses.size() - 1);
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView addressPrimaryView;
        private final TextView addressSecondaryView;

        public AddressViewHolder(@NonNull View itemView)
        {
            super(itemView);

            ItemAddressBinding binding = ItemAddressBinding.bind(itemView);

            addressPrimaryView = binding.addressPrimaryView;
            addressSecondaryView = binding.addressSecondaryView;

        }
    }
}
