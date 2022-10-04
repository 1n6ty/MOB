package com.example.mobv2.adapters;

import android.content.SharedPreferences;
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
import com.example.mobv2.models.Address;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder>
{
    private final MainActivity mainActivity;
    private final List<AddressItem> addressItems;

    private AddressItem lastItem;

    public AddressesAdapter(MainActivity mainActivity,
                            List<Address> addresses)
    {
        this.mainActivity = mainActivity;
        this.addressItems = new ArrayList<>();

        for (Address address : addresses)
        {
            addressItems.add(new AddressItem(address, false));
        }
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
        AddressItem addressItem = addressItems.get(position);
        Address address = addressItem.getAddress();

        holder.addressPrimaryView.setText(address.getPrimary());
        holder.addressSecondaryView.setText(address.getSecondary());

        holder.itemView.setOnClickListener(view -> onAddressItemClick(position));

        holder.itemView.setBackgroundResource(R.drawable.background_item_address_selector);

        if (address.getId()
                   .equals(mainActivity.getPrivatePreferences()
                                       .getString(MainActivity.ADDRESS_ID_KEY, "")))
        {
            addressItem.setChecked(true);
            lastItem = addressItem;
        }

        if (addressItem.isChecked())
        {
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.background_item_address_selected);
        }
    }

    private void onAddressItemClick(int position)
    {
        AddressItem addressItem = addressItems.get(position);

        if (lastItem != null)
        {
            if (lastItem.equals(addressItem))
                return;
            lastItem.setChecked(false);
        }

        addressItem.setChecked(true);
        Address address = addressItem.getAddress();
        MainActivity.MOB_SERVER_API.setLocation(new SetAddressCallback(mainActivity), address.getId(), MainActivity.token);
        SharedPreferences.Editor editor = mainActivity.getPrivatePreferences()
                                                      .edit();
        editor.putString(MainActivity.ADDRESS_ID_KEY, address.getId());
        editor.putString(MainActivity.ADDRESS_FULL_KEY, address.toString());
        editor.apply();

        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
        mainFragmentViewModel.setAddressChanged(true);

        lastItem = addressItem;
        notifyItemRangeChanged(0, addressItems.size());
    }

    @Override
    public int getItemCount()
    {
        return addressItems.size();
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

    protected static class AddressItem
    {
        private Address address;
        private boolean checked;

        public AddressItem(Address address,
                           boolean checked)
        {
            this.address = address;
            this.checked = checked;
        }

        public Address getAddress()
        {
            return address;
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
