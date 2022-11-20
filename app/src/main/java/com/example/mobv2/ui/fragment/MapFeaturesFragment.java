package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMapFeaturesBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;

public class MapFeaturesFragment extends BaseFragment<FragmentMapFeaturesBinding>
        implements HavingToolbar
{
    public MapFeaturesFragment()
    {
        super(R.layout.fragment_map_features);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
    }

    public void initToolbar()
    {
        super.initToolbar(binding.toolbar, R.string.menu_map_features);
    }
}