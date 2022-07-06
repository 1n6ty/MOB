package com.example.mobv2.ui.fragments.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.PostAdapter;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.models.Post;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
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
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class MainFragment extends BaseFragment<FragmentMainBinding>
{
    public static final int ZOOM = 18;

    private MainFragmentViewModel viewModel;

    private Toolbar toolbar;

    private NavDrawer navDrawer;
    private BottomSheetBehavior<View> sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecycler;

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

        initViewModel();

        initToolbar();
        initMap();
        if (viewModel.isMapReady())
        {
            setMapMarkers();
        }

        initBottomSheet();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        refreshMarker();
        googleMap.clear();
        viewModel.getPostIds()
                 .clear();
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
        sheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_NONE);

        sheetBehavior.addBottomSheetCallback(
                new PostsSheetCallback(binding.postsAppBarContainer,
                        this::initPostsRecycler,
                        () ->
                        {
                            // fix will be better
                            refreshMarker();
                        }));

        sheetBehavior.setPeekHeight(200);
        sheetBehavior.setHalfExpandedRatio(0.6f);

        postsToolbar.setNavigationOnClickListener(
                v -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        postsToolbar.setOnMenuItemClickListener(item -> false);
    }

    private void initPostsRecycler()
    {
        postsRecycler = binding.postsRecycler;

        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PostAdapter adapter = new PostAdapter(getContext(), new ArrayList<>());
        postsRecycler.setAdapter(adapter);

        for (int id : viewModel.getPostIds())
        {
            MainActivity.MOB_SERVER_API.getPost(new MOBServerAPI.MOBAPICallback()
            {
                @Override
                public void funcOk(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());

                    LinkedTreeMap<String, Object> response =
                            (LinkedTreeMap<String, Object>) obj.get("response");

                    Post post = Post.parseFromMap(response);
                    adapter.addPost(post);
                }

                @Override
                public void funcBad(LinkedTreeMap<String, Object> obj)
                {
                    Log.v("DEBUG", obj.toString());
//                    Toast.makeText(getContext(), "Post is not available", Toast.LENGTH_LONG)
//                         .show();
                }

                @Override
                public void fail(Throwable obj)
                {
                    Log.v("DEBUG", obj.toString());
//                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_LONG)
//                         .show();
                }
            }, id, MainActivity.token);
        }
    }

    private void onMapReady(GoogleMap googleMap)
    {
        viewModel.setMapReady(true);

        this.googleMap = googleMap;

        setMapMarkers();

        googleMap.setOnMarkerClickListener(this::onMarkerClick);
        googleMap.setOnMapClickListener(this::onMapClick);
    }

    private void setMapMarkers()
    {
        BitmapDescriptor descriptor = getBitmapDescriptor(R.drawable.ic_marker_24dp);

        /*MarkerAddition[] markerAdditions =
                new MarkerAddition[]{
                        new MarkerAddition(-34, 151, "Sydney", descriptor),
                        new MarkerAddition(55, 37, "Moscow", descriptor),
                        new MarkerAddition(51.5406, 46.0086, "Saratov", descriptor)
                };

        for (MarkerAddition markerAddition : markerAdditions)
        {
            googleMap.addMarker(markerAddition.create());
        }*/

        MainActivity.MOB_SERVER_API.getMarks(new MOBServerAPI.MOBAPICallback()
        {
            @Override
            public void funcOk(LinkedTreeMap<String, Object> obj)
            {
                LinkedTreeMap<String, LinkedTreeMap<String, Double>> response =
                        (LinkedTreeMap<String, LinkedTreeMap<String, Double>>) obj.get("response");

                for (String postId : response.keySet())
                {
                    viewModel.addPostId(Integer.parseInt(postId));
                    LinkedTreeMap<String, Double> coordinates = response.get(postId);
                    double x = coordinates.get("x");
                    double y = coordinates.get("y");
                    googleMap.addMarker(new MarkerAddition(x, y, "The mark", descriptor).create());

                }
            }

            @Override
            public void funcBad(LinkedTreeMap<String, Object> obj)
            {
                Toast.makeText(getContext(), "There are not markers, yet", Toast.LENGTH_LONG)
                     .show();
            }

            @Override
            public void fail(Throwable obj)
            {

            }
        }, MainActivity.token);
    }

    private boolean onMarkerClick(@NonNull Marker marker)
    {
        viewModel.setMarker(marker);

        viewModel.getMarker()
                 .setIcon(getBitmapDescriptor(R.drawable.ic_marker_36dp));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getMarker()
                                                                           .getPosition(), ZOOM));
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
        postsToolbar.setTitle(viewModel.getMarker()
                                       .getTitle());

//        MainActivity.MOB_SERVER_API.getPost();

        return true;
    }

    private void onMapClick(LatLng latLng)
    {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        refreshMarker();
    }

    private void refreshMarker()
    {
        if (viewModel.getMarker() != null)
        {
            viewModel.getMarker()
                     .setIcon(getBitmapDescriptor(R.drawable.ic_marker_24dp));
            viewModel.setMarker(null);
        }
    }
}