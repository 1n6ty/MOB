package com.example.mobv2.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentEditProfileBinding;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.MainActivity;

public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding> implements HavingToolbar
{
    private UserImpl user;

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
        MainActivity.loadImageInView(user.getAvatarUrl(), getView(), binding.avatarView);

        AsyncTask.execute(() -> super.initToolbar(binding.toolbar, user.getFullName()));

        binding.toolbar.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.menu_set_new_photo:
                    break;
                case R.id.menu_save_to_gallery:
                    break;
                case R.id.menu_delete_photo:
                    break;
                case R.id.menu_log_out:
                    return logOutMenuItemClick(item);
            }

            return true;
        });
    }

    private boolean logOutMenuItemClick(MenuItem item)
    {
        var userDao = mainActivity.appDatabase.userDao();
        var lastLoginUser = userDao.getLastLoginOne();
        lastLoginUser.setLastLogin(false);
        userDao.update(lastLoginUser);
        mainActivity.recreate();
        return true;
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