package com.example.mobv2.ui.fragments;

import android.os.AsyncTask;
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
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.main.MainFragment;

import java.util.List;
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
        MainActivity.refresh = (String) map.get("refresh");

        UserImpl user =
                new UserImpl.UserBuilder().parseFromMap((Map<String, Object>) map.get("user"));

        AsyncTask.execute(() -> mainActivity.appDatabase.userDao()
                                                        .insert(user));

        var addressesMapList = (List<Map<String, Object>>) map.get("addresses");
        AsyncTask.execute(() ->
        {
            for (Map<String, Object> addressMap : addressesMapList)
            {
                mainActivity.appDatabase.addressDao()
                                        .insert(new AddressImpl.AddressBuilder().parseFromMap(addressMap));
            }
        });

//        AsyncTask.execute(() ->
//        {
//            while (!Thread.interrupted())
//            {
//                try
//                {
//                    Thread.sleep(6000);
//
//                    mainActivity.mobServerAPI.refreshToken(new MOBServerAPI.MOBAPICallback()
//                    {
//                        @Override
//                        public void funcOk(LinkedTreeMap<String, Object> obj)
//                        {
//                            Log.v("DEBUG", obj.toString());
//
//                            var response = (LinkedTreeMap<String, Object>) obj.get("response");
//
//                            MainActivity.token = (String) response.get("token");
//                            MainActivity.refresh = (String) response.get("refresh");
//                        }
//
//                        @Override
//                        public void funcBad(LinkedTreeMap<String, Object> obj)
//                        {
//                            Log.v("DEBUG", obj.toString());
//                        }
//
//                        @Override
//                        public void fail(Throwable obj)
//                        {
//                            Log.v("DEBUG", obj.toString());
//                        }
//                    }, MainActivity.refresh, MainActivity.token);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public interface Callback
    {
        void parseUserInfoFromMapAndAddToLocalDatabase(Map<String, Object> map);
    }
}
