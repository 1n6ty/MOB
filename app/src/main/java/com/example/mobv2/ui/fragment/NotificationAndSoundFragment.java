package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentNotificationAndSoundBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;

public class NotificationAndSoundFragment extends BaseFragment<FragmentNotificationAndSoundBinding>
        implements HavingToolbar
{
    public NotificationAndSoundFragment()
    {
        super(R.layout.fragment_notification_and_sound);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
    }

    public void initToolbar()
    {
        super.initToolbar(binding.toolbar, R.string.menu_notification_and_sound);
    }
}