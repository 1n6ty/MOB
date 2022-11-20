package com.example.mobv2.ui.fragment;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.adapter.MarkersAdapter;
import com.example.mobv2.callback.AuthCallback;
import com.example.mobv2.callback.abstraction.AuthOkCallback;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.model.abstraction.Address;
import com.example.mobv2.ui.activity.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AuthFragment extends BaseFragment<FragmentAuthBinding> implements AuthOkCallback
{
    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initNextButton();
    }

    private void initNextButton()
    {
        Button nextButton = binding.nextButton;

        nextButton.setOnClickListener(this::onNextButtonClick);

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v ->
        {
            var authCallback = new AuthCallback(mainActivity);
            authCallback.setOkCallback(this::parseUserInfoFromMapAndAddToLocalDatabase);
            mainActivity.mobServerAPI.me(authCallback, MainActivity.token.isEmpty()
                    ? mainActivity.getPrivatePreferences()
                                  .getString("TOKEN", "")
                    : MainActivity.token);
        });
    }

    private void onNextButtonClick(View view)
    {
        final int DELAY = 3000;
        String loginText = binding.loginView.getText()
                                    .toString()
                                    .trim();
        String passwordText = binding.passwordView.getText()
                                          .toString();

        View errorView;
        if (loginText.isEmpty()) errorView = binding.errorLoginView;
        else if (passwordText.isEmpty()) errorView = binding.errorPasswordView;
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
        // TODO simplify
        if (map.containsKey("token") && map.containsKey("refresh"))
        {
            MainActivity.token = (String) map.get("token");
            MainActivity.refresh = (String) map.get("refresh");

            mainActivity.getPrivatePreferences()
                        .edit()
                        .putString("TOKEN", MainActivity.token)
                        .putString("REFRESH", MainActivity.refresh)
                        .apply();
        }

        var user = new UserImpl.UserBuilder().parseFromMap((Map<String, Object>) map.get("user"));

        AsyncTask.execute(() ->
        {
            var addressDao = mainActivity.appDatabase.addressDao();
            var userDao = mainActivity.appDatabase.userDao();
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
        super.updateWindow(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, mainActivity.getAttributeColor(R.attr.backgroundSecondaryWindow));
    }
}
