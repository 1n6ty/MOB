package com.example.mobv2.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding binding;

    private MainFragment mainFragment;
    private FrameLayout navContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        binding.constraintLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(binding.getRoot());

        initNavContentFrame();
        initMainFragment();
    }

    private void initMainFragment()
    {
        mainFragment = new MainFragment();
        transactionToFragment(new AuthFragment());
    }

    private void initNavContentFrame()
    {
        navContentFrame = binding.navContentFrame;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == androidx.constraintlayout.widget.R.id.home)
        {
            onBackPressed();
        }

        return true;
    }

    public void transactionToFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(navContentFrame.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentAtFrame() instanceof MainFragment)
        {
            super.onBackPressed();
            return;
        }

        transactionToFragment(mainFragment);
    }

    public Fragment getFragmentAtFrame()
    {
        return getSupportFragmentManager().findFragmentById(navContentFrame.getId());
    }

    public MainFragment getMainFragment()
    {
        return mainFragment;
    }
}