package com.example.mobv2.ui.fragments.main;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobv2.R;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.models.Post;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.GetMarksCallback;
import com.example.mobv2.ui.callbacks.GetPostCallback;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.callbacks.SetAddressCallback;
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

import java.net.MalformedURLException;
import java.net.URL;
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

        // get address on launch
        MainActivity.MOB_SERVER_API.setAddress(new SetAddressCallback(getContext()),
                mainActivity.getPrivatePreferences()
                            .getInt(MainActivity.ADDRESS_ID_KEY, -1), MainActivity.token);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        refreshMarker();
        googleMap.clear();
        viewModel.getPostsWithMarks()
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
             .into(new CustomTarget<Bitmap>()
             {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource,
                                             @Nullable Transition<? super Bitmap> transition)
                 {
                     resource.reconfigure(36, 36, Bitmap.Config.ARGB_8888);
                     Drawable drawable = new BitmapDrawable(getResources(), resource);
                     toolbar.setNavigationIcon(drawable);
                 }

                 @Override
                 public void onLoadCleared(@Nullable Drawable placeholder)
                 {

                 }
             });

        super.initToolbar(toolbar, "", v -> navDrawer.open());
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

        sheetBehavior.addBottomSheetCallback(new PostsSheetCallback(binding.postsAppBarContainer));

        sheetBehavior.setPeekHeight(200);
        sheetBehavior.setHalfExpandedRatio(0.5f);

        postsToolbar.setNavigationOnClickListener(view ->
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            refreshMarker();
        });
        postsToolbar.setOnMenuItemClickListener(item ->
        {
            refreshPosts();
            return true;
        });

        viewModel.getTitleMarker()
                 .observe(mainActivity, title ->
                 {
                     if (title != null)
                     {
                         if (viewModel.getMarker() != null) viewModel.getMarker()
                                                                     .setTitle(title);
                         postsToolbar.setTitle(title);
                     }
                 });
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
        BitmapDescriptor addressDescriptor =
                getBitmapDescriptor(R.drawable.ic_marker_address_24dp);
        BitmapDescriptor markDescriptor = getBitmapDescriptor(R.drawable.ic_marker_24dp);

        SharedPreferences preferences = mainActivity.getPrivatePreferences();
        String title = preferences.getString(MainActivity.ADDRESS_FULL_KEY, "");
        double x = preferences.getFloat(MainActivity.ADDRESS_X_KEY, -1);
        double y = preferences.getFloat(MainActivity.ADDRESS_Y_KEY, -1);
        googleMap.addMarker(new MarkerAddition(title, x, y, addressDescriptor).create())
                 .setTag(true);

        MainActivity.MOB_SERVER_API.getMarks(new GetMarksCallback(getContext(), markDescriptor, viewModel, googleMap), MainActivity.token);
    }

    private boolean onMarkerClick(@NonNull Marker marker)
    {
        refreshMarker();
        viewModel.setMarker(marker);
        viewModel.setAddressMarker((Boolean) marker.getTag());
        refreshPosts();

        setMarkerIcon(R.drawable.ic_marker_address_36dp, R.drawable.ic_marker_36dp);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getMarker()
                                                                           .getPosition(), ZOOM));

        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        postsToolbar.setTitle(viewModel.getMarker()
                                       .getTitle());

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
            setMarkerIcon(R.drawable.ic_marker_address_24dp, R.drawable.ic_marker_24dp);
            viewModel.setMarker(null);
        }
    }

    private void refreshPosts()
    {
        postsRecycler = binding.postsRecycler;
        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        var posts = new ArrayList<Post>();
        var postsAdapter = new PostsAdapter(mainActivity, posts, viewModel::setTitleMarker);
        postsRecycler.setAdapter(postsAdapter);

        for (var postWithMark : viewModel.getPostsWithMarks())
        {
            LatLng position = postWithMark.getPosition();
            if (position.equals(viewModel.getMarker()
                                         .getPosition()) || viewModel.isAddressMarker())
            {
                MainActivity.MOB_SERVER_API.getPost(new GetPostCallback(postsAdapter, viewModel), postWithMark.getPostId(), MainActivity.token);
            }
        }
    }

    private void setMarkerIcon(@DrawableRes int addressMarker,
                               @DrawableRes int marker)
    {
        viewModel.getMarker()
                 .setIcon(viewModel.isAddressMarker()
                         ? getBitmapDescriptor(addressMarker)
                         : getBitmapDescriptor(marker));
    }
}