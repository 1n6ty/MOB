package com.example.mobv2.ui.activities;

import static com.example.mobv2.adapters.MarkersAdapter.LOCALE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobv2.R;
import com.example.mobv2.callbacks.RefreshTokenCallback;
import com.example.mobv2.callbacks.abstractions.RefreshTokenOkCallback;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.fragments.main.MainFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import localdatabase.ApplicationDatabase;
import serverapi.MOBServerAPI;

public class MainActivity extends ThemedActivity implements RefreshTokenOkCallback
{
    private static final String ip = "http://192.168.0.104:8000";

    //WARNING UNSAFE
    public static String token = "";
    public static String refresh = "";
    public MOBServerAPI mobServerAPI;
    public ApplicationDatabase appDatabase;

    private ActivityMainBinding binding;

    private FrameLayout navContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mobServerAPI = new MOBServerAPI(ip + "/");
        appDatabase =
                Room.databaseBuilder(getApplicationContext(), ApplicationDatabase.class, "information_about_session_database")
                    .allowMainThreadQueries() // it can lock the UI because of threads
//                    .fallbackToDestructiveMigration()  // it will destroy database and create the new
                    .build();

        initViewBinding();

        initNavContentFrame();
        initAuthFragment();
    }

    private void initViewBinding()
    {
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        binding.constraintLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(binding.getRoot());
    }

    private void initNavContentFrame()
    {
        navContentFrame = binding.navContentFrame;
    }

    private void initAuthFragment()
    {
        replaceFragment(new AuthFragment());
    }

    public void goToFragment(@NonNull Fragment fragment)
    {
        goToFragment(fragment, R.animator.slide_in_left);
    }

    public void goToFragment(@NonNull Fragment fragment,
                             @AnimatorRes int enter)
    {
        String fragmentName = fragment.getClass()
                                      .getSimpleName();

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enter, android.R.animator.fade_out)
                .add(navContentFrame.getId(), fragment, fragmentName)
                .commit();
    }

    public void replaceFragment(@NonNull Fragment fragment)
    {
        replaceFragment(fragment, R.animator.slide_in_left);
    }

    public void replaceFragment(@NonNull Fragment fragment,
                                @AnimatorRes int enter)
    {
        String fragmentName = fragment.getClass()
                                      .getSimpleName();

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enter, android.R.animator.fade_out)
                .replace(navContentFrame.getId(), fragment, fragmentName)
                .commit();
    }

    public void toPreviousFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_in_right)
                .remove(getFragmentAtFrame())
                .commitNow();

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
        return getSupportFragmentManager().findFragmentById(navContentFrame.getId());
    }

    public SharedPreferences getPrivatePreferences()
    {
        return getPreferences(Context.MODE_PRIVATE);
    }

    public static void loadImageInView(String path,
                                       View withView,
                                       ImageView intoView)
    {
        URL url = getUrl(path);
        if (url == null) return;

        Glide.with(withView)
             .load(url)
             .into(intoView);
    }

    public static <T extends Target> void loadImageInView(String path,
                                                          View withView,
                                                          T customTarget)
    {
        URL url = getUrl(path);
        if (url == null) return;

        Glide.with(withView)
             .asBitmap()
             .load(url)
             .into(customTarget);
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

    public AddressImpl getOtherAddressByLatLng(@NonNull LatLng latLng)
    {
        try
        {
            Geocoder geocoder = new Geocoder(getApplicationContext(), LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            .get(0);

            AddressImpl rawAddress =
                    AddressImpl.createRawAddress(mapAddress.getCountryName(), mapAddress.getLocality(), mapAddress.getThoroughfare(), mapAddress.getFeatureName());
            rawAddress.setLatLng(latLng);
            return rawAddress;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
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
                RefreshTokenCallback refreshTokenCallback =
                        new RefreshTokenCallback(MainActivity.this);
                refreshTokenCallback.setOkCallback(MainActivity.this::refreshToken);
                mobServerAPI.refreshToken(refreshTokenCallback, MainActivity.refresh, MainActivity.token);
            }
        }, minutes * 60 * 1000, minutes * 60 * 1000);
    }

    @Override
    public void refreshToken(LinkedTreeMap<String, Object> map)
    {
        MainActivity.token = (String) map.get("token");
        MainActivity.refresh = (String) map.get("refresh");
    }
}
