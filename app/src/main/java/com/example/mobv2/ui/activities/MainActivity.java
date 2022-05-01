package com.example.mobv2.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.databinding.ActivityMainBinding;
import com.example.mobv2.ui.fragments.AuthFragment;
import com.example.mobv2.ui.fragments.main.MainFragment;

public class MainActivity extends AppCompatActivity
{
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

    public MainFragment getMainFragment()
    {
        return mainFragment;
    }
}