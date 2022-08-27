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
import com.example.mobv2.callbacks.GetAddressesCallback;
import com.example.mobv2.databinding.FragmentChangeAddressesBinding;
import com.example.mobv2.ui.activities.MainActivity;

public class ChangeAddressesFragment extends BaseFragment<FragmentChangeAddressesBinding>
{
    private Toolbar toolbar;
    private RecyclerView addressesRecycler;

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

    @Override
    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "Addresses");
    }

    private void initAddressesRecycler()
    {
        addressesRecycler = binding.addressesRecycler;
        ImageView noAddressesView = binding.noAddressesView;

        addressesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noAddressesView.setVisibility(View.VISIBLE);

        MainActivity.MOB_SERVER_API.getAddresses(new GetAddressesCallback(mainActivity, addressesRecycler, noAddressesView), MainActivity.token);
    }
}
