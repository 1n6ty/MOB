package com.example.mobv2.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.ui.fragments.MainFragment;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;

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
        transaction.replace(R.id.nav_content_frame, mainFragment);
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
//        if (navDrawer.isOpen())

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