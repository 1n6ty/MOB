package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentLanguageBinding;

public class LanguageFragment extends BaseFragment<FragmentLanguageBinding>
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

    private void initToolbar()
    {
        toolbar = binding.toolbar;
        toolbar.setTitle(R.string.menu_language);
        toolbar.setNavigationOnClickListener(v ->
        {
            requireActivity().onBackPressed();
        });
    }
}