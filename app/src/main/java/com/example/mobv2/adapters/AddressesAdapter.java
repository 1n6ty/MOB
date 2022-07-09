package com.example.mobv2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ItemAddressBinding;
import com.example.mobv2.models.Address;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.SetAddressCallback;
import com.example.mobv2.utils.abstractions.FuncParameterless;

import java.util.ArrayList;
import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder>
{
    private final Context context;
    private final List<AddressItem> addressItems;
    private final FuncParameterless<SharedPreferences> sharedPreferencesFuncParameterless;

    private AddressItem lastItem;

    public AddressesAdapter(Context context,
                            List<Address> addresses,
                            FuncParameterless<SharedPreferences> sharedPreferencesFuncParameterless
    )
    {
        this.context = context;
        this.addressItems = new ArrayList<>();
        this.sharedPreferencesFuncParameterless = sharedPreferencesFuncParameterless;

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

        holder.itemView.setBackgroundResource(R.drawable.background_item_address_selector);

        if (address.getId() == sharedPreferencesFuncParameterless.execute()
                                                                 .getInt(MainActivity.ADDRESS_ID_KEY, -1))
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

    public void onAddressItemSwiped(int position)
    {
        AddressItem addressItem = addressItems.get(position);

        if (lastItem != null)
            lastItem.setChecked(false);
        if (addressItem.isChecked())
        {
            addressItem.setChecked(false);
        }
        else
        {
            addressItem.setChecked(true);
            Address address = addressItem.getAddress();
            MainActivity.MOB_SERVER_API.setAddress(new SetAddressCallback(context), address.getId(), MainActivity.token);
            SharedPreferences.Editor editor = sharedPreferencesFuncParameterless.execute()
                                                                                .edit();
            editor.putInt(MainActivity.ADDRESS_ID_KEY, address.getId());
            editor.putString(MainActivity.ADDRESS_FULL_KEY, address.toString());
/*            editor.putString(MainActivity.ADDRESS_COUNTRY_KEY, address.getCountry());
            editor.putString(MainActivity.ADDRESS_CITY_KEY, address.getCity());
            editor.putString(MainActivity.ADDRESS_STREET_KEY, address.getStreet());
            editor.putInt(MainActivity.ADDRESS_HOUSE_KEY, address.getHouse());*/
            editor.putFloat(MainActivity.ADDRESS_X_KEY, (float) address.getX());
            editor.putFloat(MainActivity.ADDRESS_Y_KEY, (float) address.getY());
            editor.apply();
        }
        lastItem = addressItem;
        notifyDataSetChanged();
    }

    public boolean hasCheckedItem()
    {
        if (lastItem == null)
            return false;

        return lastItem.isChecked();
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
