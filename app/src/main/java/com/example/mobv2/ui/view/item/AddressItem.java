package com.example.mobv2.ui.view.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.adapter.AddressesAdapter;
import com.example.mobv2.callback.SetAddressCallback;
import com.example.mobv2.databinding.ItemAddressBinding;
import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.ui.abstraction.Item;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.main.MainFragmentViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import localDatabase.dao.AddressDao;

public class AddressItem implements Item<ItemAddressBinding>
{
    private final AddressDao addressDao;

    private final MainActivity mainActivity;
    private final AddressesAdapter addressesAdapter;

    public final AddressItemHelper addressItemHelper;

    private ItemAddressBinding addressBinding;

    public AddressItem(MainActivity mainActivity,
                             AddressesAdapter addressesAdapter,
                             AddressImpl address)
    {
        this.mainActivity = mainActivity;
        this.addressesAdapter = addressesAdapter;
        this.addressItemHelper = new AddressItemHelper(address);

        addressDao = mainActivity.appDatabase.addressDao();
    }

    @Override
    public void refreshItemBinding(@NonNull ItemAddressBinding addressBinding)
    {
        this.addressBinding = addressBinding;
        var parentView = addressBinding.getRoot();

        addressBinding.addressPrimaryView.setText(addressItemHelper.getPrimary());
        addressBinding.addressSecondaryView.setText(addressItemHelper.getSecondary());

        parentView.setOnClickListener(this::onAddressItemClick);

        parentView.setBackgroundResource(R.drawable.background_item_address_selector);

        if (addressItemHelper.isCurrent())
        {
            parentView.setSelected(true);
            parentView.setBackgroundResource(R.drawable.background_item_address_selected);
        }
    }

    private void onAddressItemClick(View view)
    {
        if (addressesAdapter.checkIfAddressEqualsClickedAddress(this))
            return;

        addressesAdapter.deselectClickedAddress();

        addressItemHelper.setCurrent(true);
        addressDao.update(addressItemHelper.getAddress());

        mainActivity.mobServerAPI.setLocation(new SetAddressCallback(mainActivity), addressItemHelper.getId(), MainActivity.token);

        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
        mainFragmentViewModel.setAddressChanged(true);
        addressesAdapter.notifyItemChanged(this);
    }

    public class AddressItemHelper
    {
        private final AddressImpl address;

        public AddressItemHelper(AddressImpl address)
        {
            this.address = address;
        }

        public void join()
        {

        }

        public void leave()
        {
        }

        public boolean compareById(AddressItemHelper addressItemHelper)
        {
            return this.address.compareById(addressItemHelper.address);
        }

        public AddressImpl getAddress()
        {
            return address;
        }

        public String getId()
        {
            return address.getId();
        }

        public String getPrimary()
        {
            return address.getPrimary();
        }

        public String getSecondary()
        {
            return address.getSecondary();
        }

        public String getCountry()
        {
            return address.getCountry();
        }

        public String getCity()
        {
            return address.getCity();
        }

        public String getStreet()
        {
            return address.getStreet();
        }

        public String getHouse()
        {
            return address.getHouse();
        }

        public UserImpl getOwner()
        {
            return address.getOwner();
        }

        public List<String> getUserIds()
        {
            return address.getUserIds();
        }

        public LatLng getLatLng()
        {
            return address.getLatLng();
        }

        public void setLatLng(LatLng latLng)
        {
            address.setLatLng(latLng);
        }

        public boolean isCurrent()
        {
            return address.isCurrent();
        }

        public void setCurrent(boolean current)
        {
            address.setCurrent(current);
        }
    }
}
