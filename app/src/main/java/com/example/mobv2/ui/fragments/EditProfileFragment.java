package com.example.mobv2.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentEditProfileBinding;
import com.example.mobv2.ui.activities.MainActivity;

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

        initSettingsPhoneNumberView();
        initSettingsNicknameView();
        initSettingsAddressesView();
    }

    @Override
    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, mainActivity.getPreferences(Context.MODE_PRIVATE)
                                               .getString(MainActivity.USER_FULLNAME_KEY, ""));
    }

    private void initSettingsPhoneNumberView()
    {
        binding.settingsPhoneNumberView.setOnClickListener(view ->
        {
        });
        binding.phoneNumberView.setText(mainActivity.getPreferences(Context.MODE_PRIVATE)
                                                    .getString(MainActivity.USER_PHONE_NUMBER_KEY, ""));
    }

    private void initSettingsNicknameView()
    {
        binding.settingsNicknameView.setOnClickListener(view ->
        {
        });
        binding.nicknameView.setText(mainActivity.getPreferences(Context.MODE_PRIVATE)
                                                 .getString(MainActivity.USER_NICKNAME_KEY, ""));
    }

    private void initSettingsAddressesView()
    {
        binding.settingsAddressesView.setOnClickListener(view -> mainActivity.transactionToFragment(new ChangeAddressesFragment()));
    }
}