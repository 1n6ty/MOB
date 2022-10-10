package com.example.mobv2.ui.fragments.main;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobv2.R;
import com.example.mobv2.adapters.MapAdapter;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.callbacks.SetAddressCallback;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.abstractions.HasToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.utils.MapView;
import com.example.mobv2.utils.SimpleTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.net.MalformedURLException;
import java.net.URL;

public class MainFragment extends BaseFragment<FragmentMainBinding>
        implements HasToolbar
{
    private SharedPreferences preferences;

    private MainFragmentViewModel viewModel;

    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior<View> sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecyclerView;

    private MapView mapView;
    private MapAdapter mapAdapter;

    public MainFragment()
    {
        super(R.layout.fragment_main);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);
        preferences = mainActivity.getPrivatePreferences();

        initViewModel();
        binding.setBindingContext(viewModel);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initMap();
        initBottomSheet();
        setAddressInToken();
    }

    @Override
    public void update()
    {
        super.update();
        viewModel.setFullname(preferences.getString(MainActivity.USER_FULLNAME_KEY, ""));
        viewModel.setAddress(preferences.getString(MainActivity.ADDRESS_FULL_KEY, "No selected address"));

        if (viewModel.isAddressChanged())
        {
            mapAdapter.onDestroy();

            initMap();
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            viewModel.setAddressChanged(false);
        }
    }

    private void setAddressInToken()
    {
        MainActivity.MOB_SERVER_API.setLocation(new SetAddressCallback(mainActivity),
                preferences.getString(MainActivity.ADDRESS_ID_KEY, ""), MainActivity.token);
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
    }

    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "", v -> navDrawer.open());

        navDrawer = new NavDrawer(mainActivity);

        viewModel.setFullname(preferences.getString(MainActivity.USER_FULLNAME_KEY, ""));
        viewModel.setAddress(preferences.getString(MainActivity.ADDRESS_FULL_KEY, "No selected address"));
        URL url;
        try
        {
            url =
                    new URL("http://192.168.0.104:8000" + preferences.getString(MainActivity.USER_AVATAR_URL_KEY, ""));
        }
        catch (MalformedURLException e)
        {
            return;
        }

        Glide.with(this)
             .asBitmap()
             .load(url)
             .into(new SimpleTarget()
             {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource,
                                             @Nullable Transition<? super Bitmap> transition)
                 {
                     int size = ((Float) mainActivity.getResources()
                                                     .getDimension(R.dimen.icon_size)).intValue();
                     Bitmap bitmap = Bitmap.createScaledBitmap(resource, size, size, false);
                     Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                     toolbar.setNavigationIcon(drawable);
                 }
             });
    }

    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void initBottomSheet()
    {
        postsToolbar = binding.postsToolbar;
        postsRecyclerView = binding.postsRecyclerView;

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.addBottomSheetCallback(new PostsSheetCallback(mainActivity, binding.framePosts, binding.bottomAppbarLayout));

        sheetBehavior.setPeekHeight(mainActivity.getWindow()
                                                .getDecorView()
                                                .getHeight() / 6);
        sheetBehavior.setHalfExpandedRatio(0.5f);

        postsToolbar.setOnMenuItemClickListener(item ->
        {
            PostsAdapter postsAdapter = (PostsAdapter) postsRecyclerView.getAdapter();
            if (postsAdapter == null) return false;
            switch (item.getItemId())
            {
                case R.id.menu_posts_reverse:
                    return postsAdapter.reverse();
                case R.id.menu_sort_by_appreciations:
                    return postsAdapter.sortByAppreciations();
                case R.id.menu_sort_by_date:
                    return postsAdapter.sortByDate();
                case R.id.menu_sort_by_comments:
                    return postsAdapter.sortByComments();
                default:
                    return false;
            }
        });
    }

    private void onMapReady(@NonNull GoogleMap googleMap)
    {
        mapView = new MapView(googleMap);
        mapAdapter =
                new MapAdapter(mainActivity, new MapAdapter.MarkersAdapterHelper(sheetBehavior, postsToolbar, postsRecyclerView));
        mapView.setAdapter(mapAdapter);
    }
}