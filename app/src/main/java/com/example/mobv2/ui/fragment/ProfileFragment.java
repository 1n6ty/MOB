package com.example.mobv2.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentProfileBinding;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.activity.mainActivity.MainActivityViewModel;

// TODO rewrite to adapter
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> implements HavingToolbar
{
    private UserImpl user;
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

        user = mainActivity.appDatabase.userDao().getCurrentOne();

        viewModel = new ViewModelProvider(mainActivity).get(MainActivityViewModel.class);
        binding.setBindingContext(viewModel);

        initToolbar();

        initSettingsAddressesView();
        initEditProfileFab();
    }

    public void initToolbar()
    {
        MainActivity.loadImageInView(user.getAvatarUrl(), getView(), binding.avatarView);

        super.initToolbar(binding.toolbar);

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
    }

    private boolean logOut()
    {
        var userDao = mainActivity.appDatabase.userDao();
        var lastLoginUser = userDao.getLastLoginOne();
        lastLoginUser.setLastLogin(false);
        userDao.update(lastLoginUser);
        mainActivity.replaceFragment(new AuthFragment());
        return true;
    }

    private void initSettingsAddressesView()
    {
        binding.settingsAddressesView.setOnClickListener(
                view -> mainActivity.goToFragment(new ChangeAddressesFragment()));
    }

    private void initEditProfileFab()
    {
        binding.editProfileFab.setOnClickListener(
                view -> mainActivity.goToFragment(new EditProfileFragment()));
    }

    @Override
    public void update()
    {
        super.update();
        user = mainActivity.appDatabase.userDao().getCurrentOne();
    }
}