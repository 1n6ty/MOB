package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentLanguageBinding;
import com.example.mobv2.ui.abstractions.HavingToolbar;

public class LanguageFragment extends BaseFragment<FragmentLanguageBinding>
        implements HavingToolbar
{
    private Toolbar toolbar;

    public LanguageFragment()
    {
        super(R.layout.fragment_language);
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
        super.initToolbar(toolbar, R.string.menu_language);
    }
}