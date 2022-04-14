package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMapFeaturesBinding;

public class MapFeaturesFragment extends Fragment
{
    private FragmentMapFeaturesBinding binding;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMapFeaturesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
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