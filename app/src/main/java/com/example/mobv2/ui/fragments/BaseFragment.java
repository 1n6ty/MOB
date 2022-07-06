package com.example.mobv2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.example.mobv2.R;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.utils.BitmapConverter;
import com.google.android.gms.maps.model.BitmapDescriptor;

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
        mainActivity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);


        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);

        binding.getRoot()
               .setBackgroundResource(R.color.white);
        return binding.getRoot();
    }

    protected void initToolbar()
    {

    }

    protected void initToolbar(Toolbar toolbar,
                               String title)
    {
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(this::defaultOnClick);
    }

    protected void initToolbar(Toolbar toolbar,
                               @StringRes int title)
    {
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(this::defaultOnClick);
    }

    protected void initToolbar(Toolbar toolbar,
                               String title,
                               View.OnClickListener onClickListener)
    {
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    protected void initToolbar(Toolbar toolbar,
                               @DrawableRes int resId,
                               View.OnClickListener onClickListener)
    {
        toolbar.setNavigationIcon(resId);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    protected BitmapDescriptor getBitmapDescriptor(@DrawableRes int resId)
    {
        return BitmapConverter.drawableToBitmapDescriptor(getResources(), resId);
    }

    private void defaultOnClick(View v)
    {
        mainActivity.onBackPressed();
    }
}
