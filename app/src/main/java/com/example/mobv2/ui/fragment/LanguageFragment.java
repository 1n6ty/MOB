package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentLanguageBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;

public class LanguageFragment extends BaseFragment<FragmentLanguageBinding>
        implements HavingToolbar
{
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
        super.initToolbar(binding.toolbar, R.string.menu_language);
    }
}