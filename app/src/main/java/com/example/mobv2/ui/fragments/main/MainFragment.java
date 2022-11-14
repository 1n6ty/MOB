package com.example.mobv2.ui.fragments.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobv2.R;
import com.example.mobv2.adapters.MarkersAdapter;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.callbacks.GetMarkersCallback;
import com.example.mobv2.callbacks.SetAddressCallback;
import com.example.mobv2.callbacks.abstractions.GetMarkersOkCallback;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.MarkerInfoImpl;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.PostsSheetCallback;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.ui.views.navigationdrawer.NavDrawer;
import com.example.mobv2.ui.views.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

import localdatabase.daos.AddressDao;
import localdatabase.daos.UserDao;

public class MainFragment extends BaseFragment<FragmentMainBinding>
        implements HavingToolbar, Toolbar.OnMenuItemClickListener, OnMapReadyCallback, GetMarkersOkCallback
{
    private MainFragmentViewModel viewModel;
    private UserDao userDao;
    private AddressDao addressDao;

    private Toolbar toolbar;
    private NavDrawer navDrawer;
    private BottomSheetBehavior<View> sheetBehavior;
    private Toolbar postsToolbar;
    private RecyclerView postsRecyclerView;

    private MapView mapView;
    private MarkersAdapter markersAdapter;

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

        initUserAndAddress();

        initViewModel();
        binding.setBindingContext(viewModel);

        return view;
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
    }

    private void initUserAndAddress()
    {
        userDao = mainActivity.appDatabase.userDao();
        addressDao = mainActivity.appDatabase.addressDao();
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
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "", v -> navDrawer.open());

        navDrawer = new NavDrawer(mainActivity);

        AsyncTask.execute(() ->
        {
            var user = userDao.getCurrentOne();
            var address = addressDao.getCurrentOne();

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
                    Bitmap bitmap = Bitmap.createScaledBitmap(resource, size, size, false);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
                new MarkersAdapter(mainActivity, new MarkersAdapter.MarkersAdapterHelper(sheetBehavior, postsToolbar, postsRecyclerView));
        mapView.setAdapter(markersAdapter);

        fillMap();
    }

    private void fillMap()
    {
        AddressImpl currentAddress = addressDao.getCurrentOne();
        if (currentAddress != null)
        {
            var addressString = currentAddress.toString();

            if (addressString.isEmpty()) return;

            // add address marker
            markersAdapter.addElement(new MarkerInfoImpl(addressString, currentAddress.getLatLng(), MarkerInfoImpl.ADDRESS_MARKER));

            // add other markers
            var callback = new GetMarkersCallback(mainActivity);
            callback.setOkCallback(this::parseMarkerInfosFromMapAndAddToMarkerInfoList);

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
            var markerInfo = new MarkerInfoImpl.MarkerInfoBuilder().parseFromMap(markerMap);
            markerInfo.setPostId(postId);

            markersAdapter.addElement(markerInfo);
        }
    }

    private void initBottomSheet()
    {
        postsToolbar = binding.postsToolbar;
        postsRecyclerView = binding.postsRecyclerView;
        setPostsToolbarListeners();

        sheetBehavior = BottomSheetBehavior.from(binding.framePosts);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.addBottomSheetCallback(new PostsSheetCallback(mainActivity, binding.framePosts, binding.bottomAppbarLayout));

        sheetBehavior.setPeekHeight(mainActivity.getWindow()
                                                .getDecorView()
                                                .getHeight() / 6);
        sheetBehavior.setHalfExpandedRatio(0.5f);

        postsToolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private void setPostsToolbarListeners()
    {
        postsToolbar.setNavigationOnClickListener(view -> markersAdapter.onMapClick());
        postsToolbar.getMenu()
                    .findItem(R.id.menu_posts_refresh)
                    .setOnMenuItemClickListener(view ->
                    {
                        markersAdapter.refreshPostsRecycler();
                        return true;
                    });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
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
    }

    @Deprecated
    private void setAddressInToken()
    {
        var address = addressDao.getCurrentOne();
        if (address != null)
            mainActivity.mobServerAPI.setLocation(new SetAddressCallback(mainActivity),
                    address.getId(), MainActivity.token);
    }

    @Override
    public void update()
    {
        super.update();

        AsyncTask.execute(() ->
        {
            var user = userDao.getCurrentOne();
            var address = addressDao.getCurrentOne();

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

        var user = userDao.getCurrentOne();
        user.setCurrent(false);
        userDao.update(user);
    }
}