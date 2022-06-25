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
import com.google.gson.internal.LinkedTreeMap;

import java.security.NoSuchAlgorithmException;

public class AuthFragment extends BaseFragment<FragmentAuthBinding>
{
    private final String TOKEN =
            "65794a705a4349364944457349434a7762334e3063794936494673334c43413458537767496d4e766257316c626e527a496a6f6765794a7762334e3058326c6b496a6f674c54457349434a6a623231745a573530637949364946746466537767496d7876593246306157397558326c6b496a6f67496a456966513d3d.9b050ae9045df53498a86d18ff5d565dd47a2455f8518ccd699a03fe8763a738";
    private MOBServerAPI mobServerAPI;

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
        connectToServer();
        super.onViewCreated(view, savedInstanceState);

        initPasswordView();
        initPhoneView();
        initNextButton();
    }

    private void connectToServer()
    {
        mobServerAPI = new MOBServerAPI("http://192.168.0.104:8000/rules/");
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
                mobServerAPI.auth(
                        new MOBServerAPI.MOBAPICallback()
                        {
                            @Override
                            public void funcOk(LinkedTreeMap<String, Object> obj)
                            {
                                Log.v("DEBUG", obj.toString());
                                mainActivity.transactionToMainFragment();
                            }

                            @Override
                            public void funcBad(LinkedTreeMap<String, Object> obj)
                            {
                                Log.v("DEBUG", obj.toString());
                                Toast.makeText(getContext(), "This user is not exist", Toast.LENGTH_LONG)
                                     .show();
                            }

                            @Override
                            public void fail(Throwable obj)
                            {
                                Log.v("DEBUG", obj.toString());
                                Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_LONG)
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
