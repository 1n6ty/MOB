package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentProfileBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.activity.mainActivity.MainActivityViewModel;

// TODO rewrite to adapter
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> implements HavingToolbar
{
    private MainActivityViewModel viewModel;

    public ProfileFragment()
    {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(mainActivity).get(MainActivityViewModel.class);
        binding.setBindingContext(viewModel);

        initToolbar();

        initSettingsAddressesView();
        initEditProfileFab();
    }

    public void initToolbar()
    {
        super.initToolbar(binding.toolbar);

        MainActivity.loadImageInView(viewModel.getAvatarUrl(), getView(), binding.avatarView);

        binding.toolbar.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.menu_set_new_photo:
                    return true;
                case R.id.menu_save_to_gallery:
                    return true;
                case R.id.menu_delete_photo:
                    return true;
                case R.id.menu_log_out:
                    return logOut();
                default:
                    return false;
            }
        });

        binding.avatarView.setOnClickListener(view -> new ImageViewerFragment());
    }

    private boolean logOut()
    {
        var userDao = mainActivity.appDatabase.userDao();
        var lastLoginUser = userDao.getLastLoginOne();
        lastLoginUser.setLastLogin(false);
        userDao.update(lastLoginUser);
        Navigator.replaceFragment(new AuthFragment());
        return true;
    }

    private void initSettingsAddressesView()
    {
        binding.settingsAddressesView.setOnClickListener(
                view -> Navigator.goToFragment(new ChangeAddressesFragment()));
    }

    private void initEditProfileFab()
    {
        binding.editProfileFab.setOnClickListener(
                view -> Navigator.goToFragment(new EditProfileFragment()));
    }
}