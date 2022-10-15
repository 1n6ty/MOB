package com.example.mobv2.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.AuthCallback;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.models.abstractions.User;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragment;

import java.util.Map;

public class AuthFragment extends BaseFragment<FragmentAuthBinding>
{
    private EditText passwordView;
    private EditText phoneNumberView;
    private TextView errorPhoneNumberView;
    private TextView errorPasswordView;

    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initPhoneView();
        initPasswordView();
        initNextButton();
    }

    @Override
    protected void updateWindow()
    {
        super.updateWindow(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, getResources().getColor(mainActivity.getAttribute(R.attr.backgroundSecondaryWindow)));
    }

    private void initPhoneView()
    {
        phoneNumberView = binding.phoneNumberView;
        errorPhoneNumberView = binding.errorPhoneNumberView;
    }

    private void initPasswordView()
    {
        passwordView = binding.passwordView;
        errorPasswordView = binding.errorPasswordView;
    }

    private void initNextButton()
    {
        Button nextButton = binding.nextButton;

        nextButton.setOnClickListener(v ->
        {
            // FIX PLEASE

            final int DELAY = 3000;
            Handler handler = new Handler();
            String phoneNumberText = phoneNumberView.getText()
                                                    .toString();
            String passwordText = passwordView.getText()
                                              .toString();

            handler.postDelayed(() ->
            {
                errorPhoneNumberView.setVisibility(View.INVISIBLE);
                errorPasswordView.setVisibility(View.INVISIBLE);
            }, DELAY);

            if (phoneNumberText.isEmpty())
            {
                errorPhoneNumberView.setVisibility(View.VISIBLE);
            }
            if (passwordText.isEmpty())
            {
                errorPasswordView.setVisibility(View.VISIBLE);
            }
            else
            {
                mainActivity.mobServerAPI.auth(new AuthCallback(mainActivity, this::parseUserInfoFromMapAndAddToLocalDatabase),
                        phoneNumberText, passwordText);
            }


        });

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v -> mainActivity.replaceFragment(new MainFragment()));
    }

    private void parseUserInfoFromMapAndAddToLocalDatabase(Map<String, Object> map)
    {
        MainActivity.token = (String) map.get("token");

        User user =
                new UserImpl.UserBuilder().parseFromMap((Map<String, Object>) map.get("user"));

        SharedPreferences.Editor editor = mainActivity.getPrivatePreferences()
                                                      .edit();
        editor.putString(MainActivity.USER_ID_KEY, user.getId());
        editor.putString(MainActivity.USER_AVATAR_URL_KEY, user.getAvatarUrl());
        editor.putString(MainActivity.USER_NICKNAME_KEY, user.getNickName());
        editor.putString(MainActivity.USER_FULLNAME_KEY, user.getFullName());
        editor.putString(MainActivity.USER_EMAIL_KEY, user.getEmail());
        editor.putString(MainActivity.USER_PHONE_NUMBER_KEY, user.getPhoneNumber());
        editor.apply();
    }

    public interface Callback
    {
        void parseUserInfoFromMapAndAddToLocalDatabase(Map<String, Object> map);
    }
}
