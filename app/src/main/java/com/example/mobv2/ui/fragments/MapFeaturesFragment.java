package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMapFeaturesBinding;

public class MapFeaturesFragment extends BaseFragment<FragmentMapFeaturesBinding>
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

    private void initToolbar()
    {
        toolbar = binding.toolbar;
        toolbar.setTitle(R.string.menu_map_features);
        toolbar.setNavigationOnClickListener(v ->
        {
            requireActivity().onBackPressed();
        });
    }
}