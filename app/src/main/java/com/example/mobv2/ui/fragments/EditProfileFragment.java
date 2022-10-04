package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentEditProfileBinding;
import com.example.mobv2.ui.abstractions.HasToolbar;
import com.example.mobv2.ui.activities.MainActivity;

import java.net.MalformedURLException;
import java.net.URL;

public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding>
        implements HasToolbar
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

    public void initToolbar()
    {
        toolbar = binding.toolbar;

        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + mainActivity.getPrivatePreferences()
                                                                    .getString(MainActivity.USER_AVATAR_URL_KEY, ""));
        }
        catch (MalformedURLException e)
        {
            return;
        }

        Glide.with(this)
             .load(url)
             .into(binding.avatarView);

        super.initToolbar(toolbar, mainActivity.getPrivatePreferences()
                                               .getString(MainActivity.USER_FULLNAME_KEY, ""));
    }

    private void initSettingsPhoneNumberView()
    {
        binding.settingsPhoneNumberView.setOnClickListener(view ->
        {
        });
        binding.phoneNumberView.setText(mainActivity.getPrivatePreferences()
                                                    .getString(MainActivity.USER_PHONE_NUMBER_KEY, ""));
    }

    private void initSettingsNicknameView()
    {
        binding.settingsNicknameView.setOnClickListener(view ->
        {
        });
        binding.nicknameView.setText(mainActivity.getPrivatePreferences()
                                                 .getString(MainActivity.USER_NICKNAME_KEY, ""));
    }

    private void initSettingsAddressesView()
    {
        binding.settingsAddressesView.setOnClickListener(view -> mainActivity.goToFragment(new ChangeAddressesFragment()));
    }
}