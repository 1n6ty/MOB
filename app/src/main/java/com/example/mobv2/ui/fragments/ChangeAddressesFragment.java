package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.AddressesAdapter;
import com.example.mobv2.callbacks.GetAddressesCallback;
import com.example.mobv2.databinding.FragmentChangeAddressesBinding;
import com.example.mobv2.models.MyAddress;
import com.example.mobv2.ui.abstractions.HasToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class ChangeAddressesFragment extends BaseFragment<FragmentChangeAddressesBinding>
        implements HasToolbar
{
    private Toolbar toolbar;
    private RecyclerView addressesRecyclerView;
    private ImageView noAddressesView;
    private AddressesAdapter addressAdapter;

    public ChangeAddressesFragment()
    {
        super(R.layout.fragment_change_addresses);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initAddressesRecycler();
    }

    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "Addresses");
    }

    private void initAddressesRecycler()
    {
        addressesRecyclerView = binding.addressesRecyclerView;
        noAddressesView = binding.noAddressesView;
        addressAdapter = new AddressesAdapter(mainActivity);

        addressesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressesRecyclerView.setAdapter(addressAdapter);
        noAddressesView.setVisibility(View.VISIBLE);

        MainActivity.MOB_SERVER_API.me(new GetAddressesCallback(mainActivity, this::parseAddressesFromMapListAndAddToAddresses), MainActivity.token);
    }

    private void parseAddressesFromMapListAndAddToAddresses(List<LinkedTreeMap<String, Object>> mapList)
    {
        for (LinkedTreeMap<String, Object> item : mapList)
        {
            MyAddress address = new MyAddress.AddressBuilder().parseFromMap(item);
            addressAdapter.addAddress(address);
        }

        noAddressesView.setVisibility(addressAdapter.getItemCount() < 1
                ? View.VISIBLE
                : View.INVISIBLE);
    }

    public interface Callback
    {
        void parseAddressesFromMapListAndAddToAddresses(List<LinkedTreeMap<String, Object>> mapList);
    }
}
