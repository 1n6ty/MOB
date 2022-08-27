package com.example.mobv2.callbacks;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.adapters.AddressesAdapter;
import com.example.mobv2.models.Address;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class GetAddressesCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final RecyclerView recyclerView;
    private final View noAddressesView;

    public GetAddressesCallback(MainActivity mainActivity,
                                RecyclerView recyclerView,
                                View noAddressesView)
    {
        this.mainActivity = mainActivity;
        this.recyclerView = recyclerView;
        this.noAddressesView = noAddressesView;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());

        ArrayList<Address> addresses = new ArrayList<>();

        List<LinkedTreeMap<String, Object>> response =
                (List<LinkedTreeMap<String, Object>>) obj.get("response");

        for (LinkedTreeMap<String, Object> item : response)
        {
            Address address = Address.parseFromMap(item);
            addresses.add(address);
        }

        recyclerView.setAdapter(new AddressesAdapter(mainActivity, addresses));
        noAddressesView.setVisibility(addresses.size() < 1
                ? View.VISIBLE
                : View.INVISIBLE);
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Log.v("DEBUG", obj.toString());
    }

    @Override
    public void fail(Throwable obj)
    {
        Log.v("DEBUG", obj.toString());
    }
}
