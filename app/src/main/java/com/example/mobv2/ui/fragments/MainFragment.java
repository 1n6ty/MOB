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
import com.example.mobv2.databaseimprovisation.Database;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.utils.BitmapConverter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainFragment extends Fragment
{

    private FragmentMainBinding binding;

    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecycler;
    private AppBarLayout postsAppBar;
    private View dragger;

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

        initToolbar();
        initMap();
        initBottomSheet();
        initPostsRecycler();
    }

    private void initToolbar()
    {
        dragger = binding.dragger;
        toolbar = binding.toolbar;
        navDrawer = new NavDrawer((MainActivity) requireActivity());

        // a half-measure
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_avatar);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 72, 72, false);

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
            mapFragment.getMapAsync(
                    googleMap ->
                    {
                        BitmapDescriptor descriptor =
                                BitmapConverter.drawableToBitmapDescriptor(getContext(), R.drawable.ic_marker);


                        MarkerOptions[] markerOptions =
                                new MarkerOptions[]{
                                        new MarkerOptions()
                                                .position(new LatLng(-34, 151))
                                                .title("Sydney")
                                                .icon(descriptor),
                                        new MarkerOptions()
                                                .position(new LatLng(55, 37))
                                                .title("Moscow")
                                                .icon(descriptor)};
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        {
//                            googleMap.addCircle(new CircleOptions().center(sydney).radius(100000)
//                                    .strokeColor(requireActivity().getColor(R.color.blue_500)));
//                        }

                        for (MarkerOptions option : markerOptions)
                        {
                            googleMap.addMarker(option);
                        }

                        googleMap.setOnMarkerClickListener(
                                marker ->
                                {
                                    sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                                    postsToolbar.setTitle(marker.getTitle());

                                    return true;
                                });
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    });
        }
    }

    private void initBottomSheet()
    {
        postsAppBar = binding.postsAppBar;
        postsToolbar = binding.postsToolbar;

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);

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

        postsToolbar.setNavigationOnClickListener(v ->
        {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
            {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void initPostsRecycler()
    {
        postsRecycler = binding.postsRecycler;

        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecycler.setAdapter(new PostAdapter(Database.postsDb));
    }
}