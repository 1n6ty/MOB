package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentNotificationAndSoundBinding;

public class NotificationAndSoundFragment extends Fragment
{

    private FragmentNotificationAndSoundBinding binding;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentNotificationAndSoundBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        toolbar = binding.toolbar;

        initToolbar();
    }

    private void initToolbar()
    {
        toolbar.setTitle(R.string.menu_notification_and_sound);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}