package com.example.mobv2.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.PostAdapter;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainFragment extends Fragment
{

    private FragmentMainBinding binding;

    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior sheetBehavior;


    private OnMapReadyCallback callback = new OnMapReadyCallback()
    {
        @Override
        public void onMapReady(GoogleMap googleMap)
        {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.setOnMarkerClickListener(
                    marker ->
                    {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        binding.toolbarPosts.setTitle(marker.getTitle());

                        return true;
                    });
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navDrawer = new NavDrawer((MainActivity) requireActivity());

        initToolbar();
        initMap();
        initBottomSheet();
        initRecycler();
    }

    private void initToolbar()
    {
        // a half-measure
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_avatar);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 72, 72, false);

        toolbar = binding.toolbar;
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(new BitmapDrawable(getResources(), bitmapScaled));
        toolbar.setNavigationOnClickListener(v ->
        {
            navDrawer.open();
        });
    }

    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(callback);
        }
    }

    private void initBottomSheet()
    {
        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initRecycler()
    {
        RecyclerView postsRecyclerView = binding.recyclerPosts;

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(new PostAdapter(10));
    }
}