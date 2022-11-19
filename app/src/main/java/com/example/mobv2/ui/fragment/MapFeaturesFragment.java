package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMapFeaturesBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;

public class MapFeaturesFragment extends BaseFragment<FragmentMapFeaturesBinding>
        implements HavingToolbar
{
    private Toolbar toolbar;

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
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, R.string.menu_map_features);
    }
}