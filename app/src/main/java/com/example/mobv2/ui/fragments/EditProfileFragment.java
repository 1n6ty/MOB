package com.example.mobv2.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentEditProfileBinding;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;

public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding>
        implements HavingToolbar
{
    private UserImpl user;

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

        user = mainActivity.appDatabase.userDao()
                                       .getCurrentOne();

        initToolbar();

        initSettingsPhoneNumberView();
        initSettingsNicknameView();
        initSettingsAddressesView();
    }

    public void initToolbar()
    {
        toolbar = binding.toolbar;

        MainActivity.loadImageInView(user.getAvatarUrl(), getView(), binding.avatarView);

        AsyncTask.execute(() -> super.initToolbar(toolbar, user.getFullName()));
    }

    private void initSettingsPhoneNumberView()
    {
        binding.settingsPhoneNumberView.setOnClickListener(view ->
        {
        });
        AsyncTask.execute(() -> binding.loginView.setText(user.getPhoneNumber()));
    }

    private void initSettingsNicknameView()
    {
        binding.settingsNicknameView.setOnClickListener(view ->
        {
        });
        AsyncTask.execute(() -> binding.nicknameView.setText(user.getNickName()));
    }

    private void initSettingsAddressesView()
    {
        binding.settingsAddressesView.setOnClickListener(view -> mainActivity.goToFragment(new ChangeAddressesFragment()));
    }

    @Override
    public void update()
    {
        super.update();
        user = mainActivity.appDatabase.userDao()
                                       .getCurrentOne();
    }
}