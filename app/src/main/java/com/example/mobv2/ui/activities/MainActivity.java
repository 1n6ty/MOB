package com.example.mobv2.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.fragments.main.MainFragment;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends ThemedActivity
{
    //WARNING UNSAFE
    public static final MOBServerAPI MOB_SERVER_API =
            new MOBServerAPI("http://192.168.0.104:8000/");
    public static String token = "";

    // USER
    public static final String USER_ID_KEY = "USER_ID_KEY";
    public static final String USER_AVATAR_URL_KEY = "USER_AVATAR_URL_KEY";
    public static final String USER_NICKNAME_KEY = "USER_NICKNAME_KEY";
    public static final String USER_FULLNAME_KEY = "USER_FULLNAME_KEY";
    public static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    public static final String USER_PHONE_NUMBER_KEY = "USER_PHONE_NUMBER_KEY";

    // ADDRESS
    public static final String ADDRESS_ID_KEY = "ADDRESS_ID_KEY";
    public static final String ADDRESS_FULL_KEY = "ADDRESS_FULL_KEY";
    /*
    public static final String ADDRESS_COUNTRY_KEY = "ADDRESS_COUNTRY_KEY";
    public static final String ADDRESS_CITY_KEY = "ADDRESS_CITY_KEY";
    public static final String ADDRESS_STREET_KEY = "ADDRESS_CITY_KEY";
    public static final String ADDRESS_HOUSE_KEY = "ADDRESS_HOUSE_KEY";
     */

    public static MOBServerAPI.MOBAPICallback[] callbacks;

    private ActivityMainBinding binding;

    private FrameLayout navContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + path);
        }
        catch (MalformedURLException e)
        {
            System.out.println(e.getMessage());
            return;
        }

        Glide.with(withView)
             .load(url)
             .into(intoView);
    }
}