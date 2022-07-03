package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.gson.internal.LinkedTreeMap;

import java.security.NoSuchAlgorithmException;

public class AuthFragment extends BaseFragment<FragmentAuthBinding>
{
    private EditText password;
    private EditText phone;
    private Button next;

    public AuthFragment()
    {
        super(R.layout.fragment_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initPasswordView();
        initPhoneView();
        initNextButton();
    }

    private void initPasswordView()
    {
        password = binding.passwordView;
    }

    private void initPhoneView()
    {
        phone = binding.phoneView;
    }

    private void initNextButton()
    {
        next = binding.nextButton;

        next.setOnClickListener(v ->
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
                                MainActivity.token =
                                        (String) ((LinkedTreeMap<String, Object>) obj.get("response")).get("token");
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
                        phone.getText()
                             .toString(),
                        password.getText()
                                .toString());
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
        });

        // unnecessary
        binding.skipAuthButton.setOnClickListener(v -> mainActivity.transactionToMainFragment());
    }


}
