package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.AddressesAdapter;
import com.example.mobv2.databinding.FragmentChangeAddressesBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import localdatabase.daos.AddressDao;

public class ChangeAddressesFragment extends BaseFragment<FragmentChangeAddressesBinding>
        implements HavingToolbar
{
    private AddressDao addressDao;

    private Toolbar toolbar;
    private RecyclerView addressesRecyclerView;
    private ImageView noAddressesView;
    private AddressesAdapter addressAdapter;

    public ChangeAddressesFragment()
    {
        super(R.layout.fragment_change_addresses);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        addressDao = mainActivity.appDatabase.addressDao();

        return view;
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

        List<AddressImpl> addresses = addressDao.getAll();
        for (AddressImpl address : addresses)
        {
            addressAdapter.addAddress(address);
        }

        noAddressesView.setVisibility(addressAdapter.getItemCount() < 1
                ? View.VISIBLE
                : View.INVISIBLE);
    }

    @Deprecated
    public interface Callback
    {
        void parseAddressesFromMapListAndAddToAddresses(List<LinkedTreeMap<String, Object>> mapList);
    }
}
