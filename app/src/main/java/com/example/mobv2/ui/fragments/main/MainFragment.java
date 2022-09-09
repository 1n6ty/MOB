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
import com.example.mobv2.callbacks.GetMarksCallback;
import com.example.mobv2.callbacks.SetAddressCallback;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.utils.MapView;
import com.example.mobv2.utils.MarkerAddition;
import com.example.mobv2.utils.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainFragment extends BaseFragment<FragmentMainBinding>
{
    public static final int ZOOM = 16;

    private MainFragmentViewModel viewModel;

    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior<View> sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecycler;

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

        // set address only on launch
        setAddress();
    }

    @Override
    public void update()
    {
        super.update();
        viewModel.setFullname(mainActivity.getPrivatePreferences()
                                          .getString(MainActivity.USER_FULLNAME_KEY, ""));
        viewModel.setAddress(mainActivity.getPrivatePreferences()
                                         .getString(MainActivity.ADDRESS_FULL_KEY, "No selected address"));

        if (viewModel.isAddressChanged())
        {
            mapAdapter.onDestroy();

            initMap();
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            viewModel.setAddressChanged(false);
        }
    }

    private void setAddress()
    {
        MainActivity.MOB_SERVER_API.setAddress(new SetAddressCallback(getContext()),
                mainActivity.getPrivatePreferences()
                            .getInt(MainActivity.ADDRESS_ID_KEY, -1), MainActivity.token);
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
    }

    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "", v -> navDrawer.open());

        navDrawer = new NavDrawer(mainActivity);

        viewModel.setFullname(mainActivity.getPrivatePreferences()
                                          .getString(MainActivity.USER_FULLNAME_KEY, ""));
        viewModel.setAddress(mainActivity.getPrivatePreferences()
                                         .getString(MainActivity.ADDRESS_FULL_KEY, "No selected address"));
        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + mainActivity.getPrivatePreferences()
                                                                    .getString(MainActivity.USER_AVATAR_URL_KEY, ""));
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
        postsRecycler = binding.postsRecycler;

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.addBottomSheetCallback(new PostsSheetCallback(mainActivity, binding.framePosts));

        sheetBehavior.setPeekHeight(200);
        sheetBehavior.setHalfExpandedRatio(0.5f);

        postsToolbar.setOnMenuItemClickListener(item ->
        {
            PostsAdapter postsAdapter = (PostsAdapter) postsRecycler.getAdapter();
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

        Float[] addressCoordinates = new Float[2];
        boolean possible = getAddressCoordinates(addressCoordinates);
        if (possible)
        {
            mapAdapter =
                    new MapAdapter(mainActivity, new ArrayList<>(), new MapAdapter.MarkersAdapterHelper(sheetBehavior, postsToolbar, postsRecycler));
            mapView.setAdapter(mapAdapter);

            // add address marker
            String addressTitle = mainActivity.getPrivatePreferences()
                                              .getString(MainActivity.ADDRESS_FULL_KEY, "");
            mapAdapter.addMarker(new MarkerAddition(addressTitle, addressCoordinates[0], addressCoordinates[1]).create(), MarkerInfo.ADDRESS_MARKER);

            // add other markers
            MainActivity.MOB_SERVER_API.getMarks(new GetMarksCallback(mainActivity, mapAdapter), MainActivity.token);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressCoordinates[0], addressCoordinates[1]), ZOOM));
        }
    }

    @NonNull
    private Boolean getAddressCoordinates(@NonNull Float[] coordinates)
    {
        SharedPreferences preferences = mainActivity.getPrivatePreferences();
        coordinates[0] = preferences.getFloat(MainActivity.ADDRESS_X_KEY, -1);
        coordinates[1] = preferences.getFloat(MainActivity.ADDRESS_Y_KEY, -1);

        return coordinates[0] > 0 && coordinates[1] > 0;
    }
}