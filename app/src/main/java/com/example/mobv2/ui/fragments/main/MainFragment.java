package com.example.mobv2.ui.fragments.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.PostAdapter;
import com.example.mobv2.databaseimprovisation.Database;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainFragment extends BaseFragment<FragmentMainBinding>
{
    public static final int ZOOM = 18;

    private MainFragmentViewModel viewModel;

    private Toolbar toolbar;

    private NavDrawer navDrawer;
    private BottomSheetBehavior sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecycler;

    private GoogleMap googleMap;
    private Marker marker;

    public MainFragment()
    {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        initToolbar();
        initMap();
        initBottomSheet();
        initPostsRecycler();
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
    }

    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        navDrawer = new NavDrawer(mainActivity);

        super.initToolbar(toolbar, R.drawable.sample_avatar_rounded, v -> navDrawer.open());
    }


    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null && !viewModel.isMapReady())
        {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    private void initBottomSheet()
    {
        postsToolbar = binding.postsToolbar;

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);

        sheetBehavior.addBottomSheetCallback(
                new PostsSheetCallback(binding.postsAppBarContainer, this::refreshMarker));

        sheetBehavior.setPeekHeight(200);
        sheetBehavior.setHalfExpandedRatio(0.6f);

        postsToolbar.setNavigationOnClickListener(
                v -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
    }

    private void initPostsRecycler()
    {
        postsRecycler = binding.postsRecycler;

        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecycler.setAdapter(new PostAdapter(getContext(), Database.postsDb));
    }

    private void onMapReady(GoogleMap googleMap)
    {
        viewModel.setMapReady(true);

        this.googleMap = googleMap;

        setMapMarkers(googleMap);

        googleMap.setOnMarkerClickListener(this::onMarkerClick);
        googleMap.setOnMapClickListener(this::onMapClick);
    }

    private void setMapMarkers(GoogleMap googleMap)
    {
        BitmapDescriptor descriptor = getBitmapDescriptor(R.drawable.ic_marker_24dp);

        MarkerAddition[] markerAdditions =
                new MarkerAddition[]{
                        new MarkerAddition(-34, 151, "Sydney", descriptor),
                        new MarkerAddition(55, 37, "Moscow", descriptor),
                        new MarkerAddition(51.5406, 46.0086, "Saratov", descriptor)
                };

        for (MarkerAddition markerAddition : markerAdditions)
        {
            googleMap.addMarker(markerAddition.create());
        }
    }

    private boolean onMarkerClick(Marker marker)
    {
        refreshMarker();

        this.marker = marker;

        marker.setIcon(getBitmapDescriptor(R.drawable.ic_marker_36dp));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
// переход по адресу
/*        try
        {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addresses =
                    geocoder.getFromLocationName("Россия Новосибирск Ватутина 19", 1);
            double latitude = addresses.get(0)
                                       .getLatitude();
            double longitude = addresses.get(0)
                                        .getLongitude();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/


        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        postsToolbar.setTitle(marker.getTitle());

        return true;
    }

    private void onMapClick(LatLng latLng)
    {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        refreshMarker();
    }

    private void refreshMarker()
    {
        if (marker != null)
        {
            marker.setIcon(getBitmapDescriptor(R.drawable.ic_marker_24dp));
            marker = null;
        }
    }
}