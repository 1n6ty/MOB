package com.example.mobv2.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.PostAdapter;
import com.example.mobv2.databaseimprovisation.Database;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.utils.AddMarker;
import com.example.mobv2.utils.BitmapConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainFragment extends BaseFragment<FragmentMainBinding>
{
    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecycler;
    private AppBarLayout postsAppBar;
    private View dragger;
    private GoogleMap googleMap;

    public MainFragment()
    {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initMap();
        initBottomSheet();
        initPostsRecycler();
    }

    protected void initToolbar()
    {
        dragger = binding.dragger;
        toolbar = binding.toolbar;
        navDrawer = new NavDrawer(mainActivity);

        // a half-measure
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_avatar);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 72, 72, false);
        BitmapDrawable icon = new BitmapDrawable(getResources(), bitmapScaled);

        super.initToolbar(toolbar, icon, v -> navDrawer.open());
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

    private void initBottomSheet()
    {
        postsAppBar = binding.postsAppBar;
        postsToolbar = binding.postsToolbar;

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        sheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);

        sheetBehavior.addBottomSheetCallback(
                new PostsSheetCallback(
                        sheetBehavior,
                        postsAppBar,
                        postsToolbar,
                        dragger
                ));


        int actionBarHeight = getContext()
                .getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.actionBarSize})
                .getDimensionPixelSize(0, -1);

        sheetBehavior.setPeekHeight(actionBarHeight);
        sheetBehavior.setHalfExpandedRatio(0.6f);

        postsToolbar.setNavigationOnClickListener(
                v -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
    }

    private void initPostsRecycler()
    {
        postsRecycler = binding.postsRecycler;

        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecycler.setAdapter(new PostAdapter(Database.postsDb));
    }

    private void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.0415, 82.9346), 14));

        BitmapDescriptor descriptor =
                BitmapConverter.drawableToBitmapDescriptor(getContext(), R.drawable.ic_marker);


        AddMarker[] addMarkers =
                new AddMarker[]{
                        new AddMarker(-34, 151, "Sydney", descriptor),
                        new AddMarker(55, 37, "Moscow", descriptor),
                        new AddMarker(51.5406, 46.0086, "Saratov", descriptor)
                };

        for (AddMarker addMarker : addMarkers)
        {
            googleMap.addMarker(addMarker.create());
        }

        googleMap.setOnMarkerClickListener(this::onMarkerClick);
    }

    private boolean onMarkerClick(Marker marker)
    {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));

        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        postsToolbar.setTitle(marker.getTitle());

        return true;
    }
}