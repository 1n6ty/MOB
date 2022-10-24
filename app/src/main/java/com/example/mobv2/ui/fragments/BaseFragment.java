package com.example.mobv2.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
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
import com.example.mobv2.ui.abstractions.Updatable;
import com.example.mobv2.ui.activities.MainActivity;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment
    implements Updatable
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
        updateWindow();

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        return binding.getRoot();
    }

    @CallSuper
    public void update()
    {
        updateWindow();
    }

    protected void updateWindow()
    {
        updateWindow(View.SYSTEM_UI_FLAG_VISIBLE, getResources().getColor(mainActivity.getAttribute(R.attr.backgroundPrimaryWindow)));
    }

    protected void updateWindow(int visibility,
                                @ColorInt int color)
    {
        Window window = mainActivity.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.getDecorView()
                  .setSystemUiVisibility(visibility);
        }
        window.setNavigationBarColor(color);
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

    private void defaultOnClick(View v)
    {
        mainActivity.onBackPressed();
    }
}
