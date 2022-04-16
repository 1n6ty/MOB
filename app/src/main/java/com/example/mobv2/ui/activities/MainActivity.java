package com.example.mobv2.ui.activities;

import android.app.Application;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding binding;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_content_frame, new AuthFragment());
        transaction.commit();


    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//
//        navDrawer.initNavigationDrawer();
////        navDrawer.
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == androidx.constraintlayout.widget.R.id.home)
        {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentAtFrame() instanceof MainFragment)
        {
            super.onBackPressed();
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_content_frame, mainFragment).commit();

    }

    public Fragment getFragmentAtFrame()
    {
        return getSupportFragmentManager().findFragmentById(R.id.nav_content_frame);
    }

    public MainFragment getMainFragment()
    {
        if (mainFragment != null)
            return mainFragment;
        return null;
    }
}