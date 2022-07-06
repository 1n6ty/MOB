package com.example.mobv2.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.models.User;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AuthFragment extends BaseFragment<FragmentAuthBinding>
{
    private EditText passwordView;
    private EditText phoneNumberView;
    private Button nextButton;

    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            mainActivity.getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initPhoneView();
        initPasswordView();
        initNextButton();
    }

    private void initPhoneView()
    {
        phoneNumberView = binding.phoneNumberView;
    }

    private void initPasswordView()
    {
        passwordView = binding.passwordView;
    }

    private void initNextButton()
    {
        nextButton = binding.nextButton;

        nextButton.setOnClickListener(v ->
        {
            // FIX PLEASE

            final int DELAY = 3000;
            Handler handler = new Handler();
            TextView phoneView = binding.errorPhoneView;
            TextView errorPasswordView = binding.errorPasswordView;
            String phoneText = phoneNumberView.getText()
                                              .toString();
            String passwordText = passwordView.getText()
                                              .toString();

            handler.postDelayed(() ->
            {
                phoneView.setVisibility(View.INVISIBLE);
                errorPasswordView.setVisibility(View.INVISIBLE);
            }, DELAY);

            if (phoneText.isEmpty() && passwordText.isEmpty())
            {
                phoneView.setVisibility(View.VISIBLE);
                errorPasswordView.setVisibility(View.VISIBLE);
            }
            else if (phoneText.isEmpty())
            {
                phoneView.setVisibility(View.VISIBLE);
            }
            else if (passwordText.isEmpty())
            {
                errorPasswordView.setVisibility(View.VISIBLE);
            }
            else
            {
                try
                {
                    MainActivity.MOB_SERVER_API.auth(
                            new MOBServerAPI.MOBAPICallback()
                            {
                                @Override
                                public void funcOk(LinkedTreeMap<String, Object> obj)
                                {
                                    Log.v("DEBUG", obj.toString());
                                    LinkedTreeMap<String, Object> response =
                                            (LinkedTreeMap<String, Object>) obj.get("response");

                                    MainActivity.token =
                                            (String) response.get("token");

                                    User user =
                                            User.parseFromMap((Map<String, Object>) response.get("user"));

                                    SharedPreferences.Editor editor =
                                            getActivity().getPreferences(Context.MODE_PRIVATE)
                                                         .edit();
                                    editor.putInt(MainActivity.USER_ID_KEY, user.getId());
                                    editor.putString(MainActivity.USER_NICKNAME_KEY, user.getNickName());
                                    editor.putString(MainActivity.USER_FULLNAME_KEY, user.toString());
                                    editor.putString(MainActivity.USER_EMAIL_KEY, user.getEmail());
                                    editor.putString(MainActivity.USER_PHONE_NUMBER_KEY, user.getPhoneNumber());
                                    editor.apply();

                                    mainActivity.transactionToMainFragment();
                                }

                                @Override
                                public void funcBad(LinkedTreeMap<String, Object> obj)
                                {
                                    Log.v("DEBUG", obj.toString());
                                    Toast.makeText(getContext(), R.string.user_is_not_exist, Toast.LENGTH_LONG)
                                         .show();
                                }

                                @Override
                                public void fail(Throwable obj)
                                {
                                    Log.v("DEBUG", obj.toString());
                                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_LONG)
                                         .show();
                                }
                            },
                            phoneText, passwordText);
                }
                catch (NoSuchAlgorithmException e)
                {
                    e.printStackTrace();
                }
            }


        });

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v -> mainActivity.transactionToMainFragment());
    }


}
