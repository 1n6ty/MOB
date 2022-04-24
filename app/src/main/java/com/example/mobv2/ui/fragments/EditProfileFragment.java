package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding>
{
    private Toolbar toolbar;

    public EditProfileFragment()
    {
        super(R.layout.fragment_edit_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
    }


    @Override
    protected void initToolbar()
    {
        this.toolbar = binding.toolbar;
        super.initToolbar(toolbar, "Fullname");
    }

//    private void initToolbar()
//    {
//        toolbar = binding.toolbar;
//        toolbar.setTitle("Fullname");
//        toolbar.setNavigationOnClickListener(v ->
//        {
//            requireActivity().onBackPressed();
//        });
//    }
}