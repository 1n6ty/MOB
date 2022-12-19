package com.example.mobv2.ui.fragment;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.mobv2.R;
import com.example.mobv2.callback.AuthCallback;
import com.example.mobv2.callback.AutoAuthCallback;
import com.example.mobv2.callback.abstraction.AuthOkCallback;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.model.abstraction.Address;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.util.Navigator;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AuthFragment extends BaseFragment<FragmentAuthBinding> implements AuthOkCallback
{
    private String passwordText;

    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        var transitionNo = TransitionInflater.from(mainActivity)
                                             .inflateTransition(android.R.transition.no_transition);
        setExitTransition(transitionNo);
        setEnterTransition(transitionNo);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        autoAuth();
        initNextButton();
    }

    private void autoAuth()
    {
        AsyncTask.execute(() ->
        {
            var lastLoginUser = mainActivity.appDatabase.userDao().getLastLoginOne();
            if (lastLoginUser != null && lastLoginUser.getNickName() != null && lastLoginUser.getPassword() != null)
            {
                var autoAuthCallback = new AutoAuthCallback(mainActivity);
                autoAuthCallback.setOkCallback(this::parseUserInfoFromMapAndAddToLocalDatabase);
                mainActivity.mobServerAPI.auth(autoAuthCallback, lastLoginUser.getNickName(),
                        lastLoginUser.getPassword());
            }
        });
    }

    private void initNextButton()
    {
        binding.nextButton.setOnClickListener(this::onNextButtonClick);
    }

    private void onNextButtonClick(View view)
    {
        final int DELAY = 3000; // in milliseconds
        String loginText = binding.loginView.getText().toString().trim();
        passwordText = binding.passwordView.getText().toString();

        View errorView;
        if (loginText.isEmpty())
        {
            errorView = binding.errorLoginView;
        }
        else if (passwordText.isEmpty())
        {
            errorView = binding.errorPasswordView;
        }
        else
        {
            errorView = null;
        }

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


    // TODO REWORK
    @Override
    public void parseUserInfoFromMapAndAddToLocalDatabase(LinkedTreeMap<String, Object> map)
    {
        // TODO simplify
        if (map.containsKey("token") && map.containsKey("refresh"))
        {
            MainActivity.token = (String) map.get("token");
            MainActivity.refresh = (String) map.get("refresh");

            var edit = mainActivity.getPrivatePreferences().edit();
            edit.putString(MainActivity.TOKEN_KEY, MainActivity.token);
            edit.putString(MainActivity.REFRESH_KEY, MainActivity.refresh);
            edit.apply();
        }

        var user = new UserImpl.UserParser().parseFromMap((Map<String, Object>) map.get("user"));

        AsyncTask.execute(() ->
        {
            var userDao = mainActivity.appDatabase.userDao();
            var addressDao = mainActivity.appDatabase.addressDao();
            var addresses = addressDao.getAll();

            if (!user.compareById(userDao.getLastLoginOne()))
            {
                for (AddressImpl address : addresses)
                {
                    addressDao.delete(address);
                }
            }
            var lastLogin = userDao.getLastLoginOne();
            if (lastLogin != null)
            {
                lastLogin.setLastLogin(false);
                userDao.update(lastLogin);
                user.setPassword(lastLogin.getPassword());
            }

            if (binding.rememberMeCheckBox.isChecked())
            {
                user.setPassword(passwordText);
            }

            user.setLastLogin(true);
            user.setCurrent(true);
            userDao.insert(user);

            Object addressesObject = map.get("addresses");
            if (addressesObject.equals("none"))
            {
                return;
            }

            var addressesMapList = (List<Map<String, Object>>) addressesObject;

            for (var addressMap : addressesMapList)
            {
                var address = new AddressImpl.AddressBuilder().parseFromMap(addressMap);
                var latLng = getLatLngByAddress(address);
                address.setLatLng(latLng);
                var currentAddress = addressDao.getCurrentOne();
                if (address.compareById(currentAddress))
                {
                    address.setCurrent(currentAddress.isCurrent());
                }

                addressDao.insert(address);
            }
        });

        mainActivity.startRefreshingToken();
        Navigator.replaceFragment(new MainFragment());
    }

    @Nullable
    private LatLng getLatLngByAddress(@NonNull Address address)
    {
        try
        {
            var geocoder = new Geocoder(mainActivity, MainActivity.LOCALE);
            android.location.Address mapAddress = geocoder.getFromLocationName(address.toString(),
                    1).get(0);

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
        super.updateWindow(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                mainActivity.getAttributeColor(R.attr.backgroundSecondaryWindow));
    }
}
