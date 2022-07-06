package com.example.mobv2.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.main.MainFragment;

public class MainActivity extends AppCompatActivity
{
    //WARNING UNSAFE
    public static final MOBServerAPI MOB_SERVER_API =
            new MOBServerAPI("http://192.168.0.104:8000/rules/");
    public static String token = "";

    // USER
    public static final String USER_ID_KEY = "USER_ID_KEY";
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
    public static final String ADDRESS_X_KEY = "ADDRESS_X_KEY";
    public static final String ADDRESS_Y_KEY = "ADDRESS_Y_KEY";


    private ActivityMainBinding binding;

    private MainFragment mainFragment;
    private FrameLayout navContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initViewBinding();

        initNavContentFrame();
        initMainFragment();
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

    private void initMainFragment()
    {
        mainFragment = new MainFragment();
        transactionToFragment(new AuthFragment());
    }

    public void transactionToMainFragment()
    {
        transactionToFragment(mainFragment);
    }

    public void transactionToFragment(@NonNull Fragment fragment)
    {
        String fragmentName = fragment.getClass()
                                      .getSimpleName();
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.animator.slide_in_left, 0, 0, R.animator.slide_in_right)
                .replace(navContentFrame.getId(), fragment, fragmentName)
                .addToBackStack(fragmentName)
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentAtFrame() instanceof MainFragment)
        {
            finish();
            return;
        }

        super.onBackPressed();
    }

    public Fragment getFragmentAtFrame()
    {
        return getSupportFragmentManager().findFragmentById(navContentFrame.getId());
    }
}