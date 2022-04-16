package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentAuthBinding;
import com.example.mobv2.serverapi.MOBServerAPI;

public class AuthFragment extends Fragment
{
    private final String TOKEN =
            "65794a705a4349364944457349434a7762334e3063794936494673334c43413458537767496d4e766257316c626e527a496a6f6765794a7762334e3058326c6b496a6f674c54457349434a6a623231745a573530637949364946746466537767496d7876593246306157397558326c6b496a6f67496a456966513d3d.9b050ae9045df53498a86d18ff5d565dd47a2455f8518ccd699a03fe8763a738";
    private MOBServerAPI mobServerAPI;

    private FragmentAuthBinding binding;

    private EditText password;
    private Button next;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        connectToServer();

        initPasswordView();
        initNextButton();
    }

    private void connectToServer()
    {
        mobServerAPI = new MOBServerAPI("http://192.168.0.104:8000/rules/");
    }

    private void initNextButton()
    {
        next = binding.nextButton;

        next.setOnClickListener(v ->
        {
            mobServerAPI.deletePost(obj ->
                    {
                        Log.v("DEBUG", obj.toString());
                        return null;
                    }, num ->
                    {
                        Log.v("DEBUG", num.toString());
                        return null;
                    }, 7,
                    TOKEN);

            FragmentTransaction transaction =
                    requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_content_frame, new MainFragment());
            transaction.commit();
        });
    }

    private void initPasswordView()
    {
        password = binding.passwordView;
    }
}
