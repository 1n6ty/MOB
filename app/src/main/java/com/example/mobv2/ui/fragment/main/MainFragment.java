package com.example.mobv2.ui.fragment.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobv2.R;
import com.example.mobv2.adapter.MarkersAdapter;
import com.example.mobv2.adapter.PostsAdapter;
import com.example.mobv2.callback.GetMarkersCallback;
import com.example.mobv2.callback.SetAddressCallback;
import com.example.mobv2.callback.abstraction.GetMarkersOkCallback;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.model.MarkerInfoImpl;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.callback.PostsSheetCallback;
import com.example.mobv2.ui.fragment.BaseFragment;
import com.example.mobv2.ui.view.MapView;
import com.example.mobv2.ui.view.navigationDrawer.NavDrawer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

public class MainFragment extends BaseFragment<FragmentMainBinding>
        implements HavingToolbar, Toolbar.OnMenuItemClickListener, OnMapReadyCallback, GetMarkersOkCallback
{
    private MainFragmentViewModel viewModel;

    private NavDrawer navDrawer;
    private BottomSheetBehavior<View> sheetBehavior;

    private MarkersAdapter markersAdapter;
    private MapView mapView;

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

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initMap();
        setAddressInToken();
    }

    public void initToolbar()
    {
        var toolbar = binding.toolbar;
        super.initToolbar(toolbar, "", v -> navDrawer.open());

        navDrawer = new NavDrawer(mainActivity);

        AsyncTask.execute(() ->
        {
            var user = mainActivity.appDatabase.userDao()
                                               .getCurrentOne();
            var address = mainActivity.appDatabase.addressDao()
                                                  .getCurrentOne();

            viewModel.setFullname(user.getFullName());
            viewModel.setAddress(address == null ? "No selected address" : address.toString());

            MainActivity.loadImageInView(user.getAvatarUrl(), getView(), new CustomTarget<Bitmap>()
            {
                @Override
                public void onResourceReady(@NonNull Bitmap resource,
                                            @Nullable Transition<? super Bitmap> transition)
                {
                    int size = ((Float) mainActivity.getResources()
                                                    .getDimension(R.dimen.icon_size)).intValue();
                    var bitmap = Bitmap.createScaledBitmap(resource, size, size, false);
                    var drawable = new BitmapDrawable(getResources(), bitmap);
                    toolbar.setNavigationIcon(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder)
                {

                }
            });
        });
    }

    private void initMap()
    {
        var mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this::onMapReady);

            initBottomSheet();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        mapView = new MapView(googleMap);
        markersAdapter =
                new MarkersAdapter(mainActivity, new MarkersAdapter.MarkersAdapterHelper(sheetBehavior, binding.postsToolbar, binding.postsRecyclerView));
        mapView.setAdapter(markersAdapter);

        fillMap();
    }

    private void fillMap()
    {
        var currentAddress = mainActivity.appDatabase.addressDao()
                                                     .getCurrentOne();
        if (currentAddress != null)
        {
            var addressString = currentAddress.toString();

            if (addressString.isEmpty()) return;

            // add address marker
            markersAdapter.addElement(new MarkerInfoImpl(addressString, currentAddress.getLatLng(), MarkerInfoImpl.ADDRESS_MARKER));

            int fillColor =
                    mainActivity.getAttributeColorWithAlpha(androidx.appcompat.R.attr.colorAccent, 0.1f);
            int strokeColor =
                    mainActivity.getAttributeColor(androidx.appcompat.R.attr.colorPrimary);

            mapView.addCircle(new CircleOptions().center(currentAddress.getLatLng())
                                                 .radius(MarkersAdapter.CIRCLE_RADIUS)
                                                 .fillColor(fillColor)
                                                 .strokeWidth(2.5f)
                                                 .strokeColor(strokeColor));

            // add other markers
            var callback = new GetMarkersCallback(mainActivity);
            callback.setOkCallback(this::parseMarkerInfosFromMapAndAddToMarkerInfoList);
            callback.setFailCallback(this::getMarkerInfosByCurrentAddressIdFromLocalDbAndAddToMarkerInfoList);

            mainActivity.mobServerAPI.getMarks(callback, MainActivity.token);
            markersAdapter.animateCameraTo(currentAddress.getLatLng());
        }
    }

    @Override
    public void parseMarkerInfosFromMapAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map)
    {
        for (var postId : map.keySet())
        {
            var markerMap = (LinkedTreeMap<String, Object>) map.get(postId);
            markerMap.put("post_id", postId);
            var markerInfo = new MarkerInfoImpl.MarkerInfoBuilder().parseFromMap(markerMap);
            markerInfo.setAddressId(mainActivity.appDatabase.addressDao()
                                                            .getCurrentId());

            mainActivity.appDatabase.markerInfoDao()
                                    .insert(markerInfo);
            markersAdapter.addElement(markerInfo);
        }
    }

    private void getMarkerInfosByCurrentAddressIdFromLocalDbAndAddToMarkerInfoList()
    {
        var markerInfos = mainActivity.appDatabase.markerInfoDao()
                                                  .getAllByAddressId(mainActivity.appDatabase.addressDao()
                                                                                             .getCurrentId());
        if (markerInfos == null || markerInfos.isEmpty())
        {
            Toast.makeText(mainActivity, "Markers are not uploaded", Toast.LENGTH_LONG)
                 .show();
            return;
        }

        for (var markerInfo : markerInfos)
        {
            markersAdapter.addElement(markerInfo);
        }
    }

    private void initBottomSheet()
    {
        setPostsToolbarListeners();

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.addBottomSheetCallback(new PostsSheetCallback(mainActivity, binding.framePosts, binding.bottomAppbarLayout));

        sheetBehavior.setPeekHeight(mainActivity.getWindow()
                                                .getDecorView()
                                                .getHeight() / 6);
        sheetBehavior.setHalfExpandedRatio(0.5f);
    }

    private void setPostsToolbarListeners()
    {
        var postsToolbar = binding.postsToolbar;
        postsToolbar.setNavigationOnClickListener(view -> markersAdapter.onMapClick());

        binding.postsToolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        PostsAdapter postsAdapter = (PostsAdapter) binding.postsRecyclerView.getAdapter();
        if (postsAdapter == null) return false;
        switch (item.getItemId())
        {
            case R.id.menu_posts_refresh:
                markersAdapter.refreshPostsRecycler();
                return true;
            case R.id.menu_posts_reverse:
                return postsAdapter.reverse();
            case R.id.menu_sort_by_rates:
                return postsAdapter.sortByRates();
            case R.id.menu_sort_by_date:
                return postsAdapter.sortByDate();
            case R.id.menu_sort_by_comments:
                return postsAdapter.sortByComments();
            default:
                return false;
        }
    }

    @Deprecated
    private void setAddressInToken()
    {
        var address = mainActivity.appDatabase.addressDao()
                                              .getCurrentOne();
        if (address != null)
            mainActivity.mobServerAPI.setLocation(new SetAddressCallback(mainActivity), address.getId(), MainActivity.token);
    }

    @Override
    public void update()
    {
        super.update();

        AsyncTask.execute(() ->
        {
            var user = mainActivity.appDatabase.userDao()
                                               .getCurrentOne();
            var address = mainActivity.appDatabase.addressDao()
                                                  .getCurrentOne();

            viewModel.setFullname(user.getFullName());
            viewModel.setAddress(address == null ? "No selected address" : address.toString());
        });

        if (viewModel.isAddressChanged())
        {
            markersAdapter.onDestroy();

            initMap();
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            viewModel.setAddressChanged(false);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        var userDao = mainActivity.appDatabase.userDao();
        var user = userDao.getCurrentOne();
        user.setCurrent(false);
        userDao.update(user);
    }
}