package com.example.mobv2.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.AuthCallback;

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
        changeStatusBar();

        initPhoneView();
        initPasswordView();
        initNextButton();
    }

    private void changeStatusBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            mainActivity.getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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

            if (phoneNumberText.isEmpty() && passwordText.isEmpty())
            {
                errorPhoneNumberView.setVisibility(View.VISIBLE);
                errorPasswordView.setVisibility(View.VISIBLE);
            }
            else if (phoneNumberText.isEmpty())
            {
                errorPhoneNumberView.setVisibility(View.VISIBLE);
            }
            else if (passwordText.isEmpty())
            {
                errorPasswordView.setVisibility(View.VISIBLE);
            }
            else
            {
                MainActivity.MOB_SERVER_API.auth(new AuthCallback(mainActivity), phoneNumberText, passwordText);
            }


        });

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v -> mainActivity.transactionToMainFragment());
    }


}
