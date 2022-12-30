package com.example.mobv2.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mobv2.R;
import com.example.mobv2.callback.EditUserCallback;
import com.example.mobv2.databinding.FragmentEditProfileBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.activity.mainActivity.MainActivityViewModel;

public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding>
        implements HavingToolbar
{
    private MainActivityViewModel viewModel;

    public EditProfileFragment()
    {
        super(R.layout.fragment_edit_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(mainActivity).get(MainActivityViewModel.class);
        binding.setBindingContext(viewModel);

        initToolbar();
        initBody();
    }

    @Override
    public void initToolbar()
    {
        var toolbar = binding.toolbar;
        super.initToolbar(toolbar, getString(R.string.edit_profile));

        toolbar.findViewById(R.id.menu_edit_confirm).setOnClickListener(view ->
        {
            var user = mainActivity.appDatabase.userDao().getCurrentOne();
            var callback = new EditUserCallback(mainActivity);
            callback.setOkCallback(() ->
            {
                user.edit()
                    .setNickname(viewModel.getNickName())
                    .setPhoneNumber(viewModel.getPhoneNumber())
                    .setEmail(viewModel.getEmail());
                mainActivity.appDatabase.userDao().update(user);
                Navigation.findNavController(requireActivity(), R.id.nav_content_frame)
                          .popBackStack();
            });

            mainActivity.mobServerAPI.editUser(callback, null, viewModel.getNickName(), null,
                    viewModel.getEmail().equals(user.getEmail()) ? null : viewModel.getEmail(),
                    null, viewModel.getPhoneNumber().equals(user.getPhoneNumber()) ? null
                                                                                   : viewModel.getPhoneNumber(),
                    null, MainActivity.token);
        });
    }

    public void initBody()
    {
        AsyncTask.execute(() ->
        {
            var user = mainActivity.appDatabase.userDao().getCurrentOne();
            viewModel.setNickName(user.getNickName());
            viewModel.setPhoneNumber(user.getPhoneNumber());
            viewModel.setEmail(user.getEmail());
        });
    }
}
