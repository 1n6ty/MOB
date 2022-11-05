package com.example.mobv2.ui.fragments;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.adapters.MarkersAdapter;
import com.example.mobv2.callbacks.AuthCallback;
import com.example.mobv2.callbacks.abstractions.AuthOkCallback;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.models.abstractions.Address;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import localdatabase.daos.AddressDao;
import localdatabase.daos.UserDao;

public class AuthFragment extends BaseFragment<FragmentAuthBinding> implements AuthOkCallback
{
    private AddressDao addressDao;
    private UserDao userDao;

    private EditText passwordView;
    private EditText loginView;
    private TextView errorLoginView;
    private TextView errorPasswordView;

    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        addressDao = mainActivity.appDatabase.addressDao();
        userDao = mainActivity.appDatabase.userDao();

        initPhoneView();
        initPasswordView();
        initNextButton();
    }

    private void initPhoneView()
    {
        loginView = binding.loginView;
        errorLoginView = binding.errorLoginView;
    }

    private void initPasswordView()
    {
        passwordView = binding.passwordView;
        errorPasswordView = binding.errorPasswordView;
    }

    private void initNextButton()
    {
        Button nextButton = binding.nextButton;

        nextButton.setOnClickListener(this::onNextButtonClick);

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v -> mainActivity.replaceFragment(new MainFragment()));
    }

    private void onNextButtonClick(View view)
    {
        final int DELAY = 3000;
        String loginText = loginView.getText()
                                    .toString()
                                    .trim();
        String passwordText = passwordView.getText()
                                          .toString();

        View errorView;
        if (loginText.isEmpty()) errorView = errorLoginView;
        else if (passwordText.isEmpty()) errorView = errorPasswordView;
        else errorView = null;

        if (errorView != null)
        {
            errorView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(() -> errorView.setVisibility(View.INVISIBLE), DELAY);
            return;
        }

        var authCallback = new AuthCallback(mainActivity);
        authCallback.setOkCallback(this::parseUserInfoFromMapAndAddToLocalDatabase);
        mainActivity.mobServerAPI.auth(authCallback, loginText, passwordText);
    }

    @Override
    public void parseUserInfoFromMapAndAddToLocalDatabase(LinkedTreeMap<String, Object> map)
    {
        MainActivity.token = (String) map.get("token");
        MainActivity.refresh = (String) map.get("refresh");

        var user = new UserImpl.UserBuilder().parseFromMap((Map<String, Object>) map.get("user"));

        AsyncTask.execute(() ->
        {
            List<AddressImpl> addresses = addressDao.getAll();
            if (!user.compareById(userDao.getCurrentOne()))
            {
                for (AddressImpl address : addresses)
                    addressDao.delete(address);
            }

            user.setCurrent(true);
            userDao.insert(user);

            Object addressesObject = map.get("addresses");
            if (!addressesObject.equals("none"))
            {
                var addressesMapList = (List<Map<String, Object>>) addressesObject;

                for (Map<String, Object> addressMap : addressesMapList)
                {
                    AddressImpl address = new AddressImpl.AddressBuilder().parseFromMap(addressMap);
                    LatLng latLng = getLatLngByAddress(address);
                    address.setLatLng(latLng);
                    var currentAddress = addressDao.getCurrentOne();
                    if (address.compareById(currentAddress))
                    {
                        address.setCurrent(currentAddress.isCurrent());
                    }

                    addressDao.insert(address);
                }
            }
        });

        mainActivity.startRefreshingToken();
    }

    private LatLng getLatLngByAddress(Address address)
    {
        try
        {
            Geocoder geocoder = new Geocoder(mainActivity, MarkersAdapter.LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocationName(address.toString(), 1)
                            .get(0);

            return new LatLng(mapAddress.getLatitude(), mapAddress.getLongitude());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, getResources().getColor(mainActivity.getAttribute(R.attr.backgroundSecondaryWindow)));
    }
}
