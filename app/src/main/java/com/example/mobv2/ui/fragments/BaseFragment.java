package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.example.mobv2.ui.activities.MainActivity;

public class BaseFragment<T extends ViewDataBinding> extends Fragment
{
    protected MainActivity mainActivity;
    protected T binding;

    private final int layoutId;

    public BaseFragment(@LayoutRes int layoutId)
    {
        this.layoutId = layoutId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        return binding.getRoot();
    }
}
