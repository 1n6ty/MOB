package com.example.mobv2.ui.activity.mainActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobv2.callback.RefreshTokenCallback;
import com.example.mobv2.callback.abstraction.RefreshTokenOkCallback;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.ui.activity.ThemedActivity;
import com.example.mobv2.ui.fragment.AuthFragment;
import com.example.mobv2.ui.fragment.BaseFragment;
import com.example.mobv2.ui.fragment.MainFragment;
import com.example.mobv2.util.Navigator;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import localDatabase.ApplicationDatabase;
import serverAPI.MOBServerAPI;

public class MainActivity extends ThemedActivity implements RefreshTokenOkCallback
{
    //WARNING UNSAFE
    public static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    private static final String ip = "http://192.168.0.104:8000";
    public static Locale LOCALE = Locale.getDefault();
    public static String token = "";
    public static String refresh = "";
    public MOBServerAPI mobServerAPI;
    public ApplicationDatabase appDatabase;
    public Navigator navigator;

    private ActivityMainBinding binding;

    public static void loadImageInView(String path,
                                       View withView,
                                       ImageView intoView)
    {
        URL url = getUrl(path);
        if (url == null)
        {
            return;
        }

        Glide.with(withView).load(url).into(intoView);
    }

    public static <T extends Target> void loadImageInView(String path,
                                                          View withView,
                                                          T customTarget)
    {
        URL url = getUrl(path);
        if (url == null)
        {
            return;
        }

        Glide.with(withView).asBitmap().load(url).into(customTarget);
    }

    private static URL getUrl(String path)
    {
        try
        {
            return new URL(ip + path);
        }
        catch (MalformedURLException e)
        {
            Log.e("GetUrlError", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mobServerAPI = new MOBServerAPI(ip + "/");
        appDatabase = Room.databaseBuilder(getApplicationContext(), ApplicationDatabase.class,
                                  "information_about_session_database")
                          .allowMainThreadQueries() // it can lock the UI because of threads
                          .fallbackToDestructiveMigration()  // it will destroy database and create the new
                          .build();

//        navigator = new Navigator(this, binding.navContentFrame);

        initViewBinding();

        initAuthFragment();
    }

    private void initViewBinding()
    {
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        binding.constraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(binding.getRoot());
    }

    private void initAuthFragment()
    {
        replaceFragment(new AuthFragment());
    }

    public void goToFragment(@NonNull Fragment fragment)
    {
        String fragmentName = fragment.getClass().getSimpleName();

        getSupportFragmentManager().beginTransaction()
                                   .add(binding.navContentFrame.getId(), fragment, fragmentName)
                                   .commitNow();
    }

    public void goToFragmentWithSharedElement(@NonNull Fragment fragment,
                                              View sharedElement,
                                              String destinationId)
    {
        String fragmentName = fragment.getClass().getSimpleName();

        getSupportFragmentManager().beginTransaction()
                                   .addSharedElement(sharedElement, destinationId)
                                   .add(binding.navContentFrame.getId(), fragment, fragmentName)
                                   .commitNow();
    }

    public void replaceFragment(@NonNull Fragment fragment)
    {
        String fragmentName = fragment.getClass().getSimpleName();

        getSupportFragmentManager().beginTransaction()
                                   .replace(binding.navContentFrame.getId(), fragment, fragmentName)
                                   .commit();
    }

    public void toPreviousFragment()
    {
        getSupportFragmentManager().beginTransaction().remove(getFragmentAtFrame()).commitNow();

        ((BaseFragment) getFragmentAtFrame()).update();
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentAtFrame() instanceof MainFragment)
        {
            finish();
            return;
        }

        toPreviousFragment();
    }

    public Fragment getFragmentAtFrame()
    {
        return getSupportFragmentManager().findFragmentById(binding.navContentFrame.getId());
    }

    public SharedPreferences getPrivatePreferences()
    {
        return getPreferences(Context.MODE_PRIVATE);
    }

    public AddressImpl getOtherAddressByLatLng(@NonNull LatLng latLng)
    {
        try
        {
            Geocoder geocoder = new Geocoder(getApplicationContext(), LOCALE);
            android.location.Address mapAddress = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1).get(0);

            AddressImpl rawAddress = AddressImpl.createRawAddress(mapAddress.getCountryName(),
                    mapAddress.getLocality(), mapAddress.getThoroughfare(),
                    mapAddress.getFeatureName());
            rawAddress.setLatLng(latLng);
            return rawAddress;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public float[] getDistance(double startLatitude,
                               double startLongitude,
                               double endLatitude,
                               double endLongitude)
    {
        float[] distance = new float[2];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude,
                distance);

        return distance;
    }

    public void startRefreshingToken()
    {
        final int minutes = 8;

        var timer = new Timer(true);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                RefreshTokenCallback refreshTokenCallback = new RefreshTokenCallback(
                        MainActivity.this);
                refreshTokenCallback.setOkCallback(MainActivity.this::refreshToken);
                mobServerAPI.refreshToken(refreshTokenCallback, MainActivity.token,
                        MainActivity.refresh);
            }
        }, minutes * 60 * 1000, minutes * 60 * 1000);
    }

    @Override
    public void refreshToken(LinkedTreeMap<String, Object> map)
    {
        MainActivity.token = (String) map.get("token");
        MainActivity.refresh = (String) map.get("refresh");

        getPrivatePreferences().edit()
                               .putString("TOKEN", MainActivity.token)
                               .putString("REFRESH", MainActivity.refresh)
                               .apply();
    }
}
