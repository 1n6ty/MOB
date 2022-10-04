package com.example.mobv2.adapters;

import android.location.Geocoder;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.callbacks.GetMarksCallback;
import com.example.mobv2.callbacks.GetPostCallback;
import com.example.mobv2.models.Address;
import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.markercreators.MapSkillsBottomSheetFragment;
import com.example.mobv2.ui.fragments.markercreators.MarkerCreatorViewModel;
import com.example.mobv2.utils.BitmapConverter;
import com.example.mobv2.utils.MapView;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapAdapter extends MapView.Adapter
{
    public static final int ZOOM = 16;
    private final static Locale LOCALE = Locale.ENGLISH;

    private final MainActivity mainActivity;
    private GoogleMap googleMap;

    private final List<MarkerInfo> markerInfoList;
    private final MarkersAdapterHelper markersAdapterHelper;

    private MarkerInfo markerInfo;

    private final int[][] drawableIds;

    public MapAdapter(MainActivity mainActivity,
                      MarkersAdapterHelper markersAdapterHelper)
    {
        this.mainActivity = mainActivity;
        this.markerInfoList = new ArrayList<>();
        this.markersAdapterHelper = markersAdapterHelper;

        drawableIds = new int[][]{
                {R.drawable.ic_marker_address_24dp, R.drawable.ic_marker_address_36dp},
                {R.drawable.ic_marker_24dp, R.drawable.ic_marker_36dp}};
    }

    @Override
    public void onCreate(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(this::onMarkerClick);
        googleMap.setOnMapClickListener(latLng -> onMapClick());
        googleMap.setOnMapLongClickListener(this::onMapLongClick);

        Toolbar postsToolbar = markersAdapterHelper.postsToolbar;
        postsToolbar.setNavigationOnClickListener(view -> onMapClick());
        postsToolbar.getMenu()
                    .findItem(R.id.menu_posts_refresh)
                    .setOnMenuItemClickListener(view ->
                    {
                        refreshPostsRecycler();
                        return true;
                    });


        String addressString = mainActivity.getPrivatePreferences()
                                           .getString(MainActivity.ADDRESS_FULL_KEY, "");

        if (addressString.isEmpty()) return;

        LatLng addressLatLng =
                getLatLngByAddress(new Address.AddressBuilder().parseFromString(addressString));
        // add address marker
        addMarker(new MarkerAddition(addressString, addressLatLng).create(), MarkerInfo.ADDRESS_MARKER);

        // add other markers
        MainActivity.MOB_SERVER_API.getMarks(new GetMarksCallback(mainActivity, this), MainActivity.token);
        animateCameraTo(addressLatLng);
    }

    @Override
    public void onBindMarker(int position)
    {
        MarkerInfo markerInfo = markerInfoList.get(position);

        switch (markerInfo.getMarkerCondition())
        {
            case MarkerInfo.MARKER_NOT_CLICKED:
                int drawableId = drawableIds[markerInfo.getMarkerType()][0];
                BitmapDescriptor icon =
                        BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId);
                markerInfo.getMarker()
                          .setIcon(icon);
                break;
            case MarkerInfo.MARKER_CLICKED:
                drawableId = drawableIds[markerInfo.getMarkerType()][1];
                icon =
                        BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId);
                markerInfo.getMarker()
                          .setIcon(icon);
                break;
            case MarkerInfo.MARKER_ADDED:
                drawableId = drawableIds[markerInfo.getMarkerType()][0];
                icon =
                        BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId);
                markerInfo.getMarker()
                          .setIcon(icon);
                break;
            case MarkerInfo.MARKER_REMOVED:
                markerInfo.getMarker()
                          .remove();
                break;
        }
    }

    private void onMapClick()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        deselectClickedMarker();
    }

    private void onMapLongClick(@NonNull LatLng latLng)
    {
        animateCameraTo(latLng);

        initMarkerCreatorViewModel(latLng);

        Marker pointerMarker =
                googleMap.addMarker(new MarkerAddition(latLng.latitude, latLng.longitude).create());

        var bottomSheetFragment = new MapSkillsBottomSheetFragment();
        bottomSheetFragment.setOnDestroyViewListener(view -> pointerMarker.remove());
        bottomSheetFragment.show(mainActivity.getSupportFragmentManager(), MapSkillsBottomSheetFragment.class.getSimpleName());
    }

    private void initMarkerCreatorViewModel(@NonNull LatLng latLng)
    {
        var markerCreatorViewModel =
                new ViewModelProvider(mainActivity).get(MarkerCreatorViewModel.class);

        markerCreatorViewModel.setLatLng(latLng);
        markerCreatorViewModel.setCallback(new MOBServerAPI.MOBAPICallback()
        {
            @Override
            public void funcOk(LinkedTreeMap<String, Object> obj)
            {
                var response = (LinkedTreeMap<String, Object>) obj.get("response");

                int postId = ((Double) response.get("id")).intValue();
                double x = latLng.latitude;
                double y = latLng.longitude;
                Marker marker =
                        googleMap.addMarker(new MarkerAddition("The mark", x, y).create());
                MarkerInfo markerInfo =
                        new MarkerInfo(marker, MarkerInfo.SUB_ADDRESS_MARKER);
                markerInfo.getMetadata()
                          .put("post_id", postId);
                markerInfoList.add(markerInfo);
                markerInfo.setMarkerCondition(MarkerInfo.MARKER_ADDED);
                notifyItemChanged(markerInfoList.size() - 1);
            }

            @Override
            public void funcBad(LinkedTreeMap<String, Object> obj)
            {
            }

            @Override
            public void fail(Throwable obj)
            {
            }
        });

        Address address = getAddressByLatLng(latLng);

        markerCreatorViewModel.setAddress(address);
    }

    private void animateCameraTo(@NonNull LatLng latLng)
    {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
    }

    private boolean onMarkerClick(Marker marker)
    {
        deselectClickedMarker();

        for (int i = 0, markersSize = markerInfoList.size(); i < markersSize; i++)
        {
            MarkerInfo markerInfo = markerInfoList.get(i);
            if (markerInfo.getMarker()
                          .getId()
                          .equals(marker.getId()))
            {
                this.markerInfo = markerInfo;
                markerInfo.setMarkerCondition(MarkerInfo.MARKER_CLICKED);
                refreshPostsRecycler();
                refreshPosts();
                animateCameraTo(markerInfo.getMarker()
                                          .getPosition());
                notifyItemChanged(i);
                break;
            }
        }

        return true;
    }

    private void refreshPostsRecycler()
    {
        if (markerInfo == null) return;

        markersAdapterHelper.postsRecycler.setLayoutManager(new LinearLayoutManager(mainActivity));
        var postsAdapter = new PostsAdapter(mainActivity, this);
        markersAdapterHelper.postsRecycler.setAdapter(postsAdapter);

        if (markerInfo.getMarkerType() == MarkerInfo.ADDRESS_MARKER)
        {
            for (var markerInfoItem : markerInfoList)
            {
                if (markerInfoItem.getMarkerType() == MarkerInfo.SUB_ADDRESS_MARKER)
                {
                    MainActivity.MOB_SERVER_API.getPost(new GetPostCallback(markersAdapterHelper.postsRecycler), String.valueOf(markerInfoItem.getMetadata()
                                                                                                                                              .get("post_id")), MainActivity.token);
                }
            }
        }
        else if (markerInfo.getMarkerType() == MarkerInfo.SUB_ADDRESS_MARKER)
        {
            MainActivity.MOB_SERVER_API.getPost(new GetPostCallback(markersAdapterHelper.postsRecycler), String.valueOf(markerInfo.getMetadata()
                                                                                                                                  .get("post_id")), MainActivity.token);
        }
    }

    private void refreshPosts()
    {
        if (markerInfo == null) return;

        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        final boolean isAddressMarker =
                markerInfo.getMarkerType() == MarkerInfo.ADDRESS_MARKER;
        Menu postsToolbarMenu = markersAdapterHelper.postsToolbar.getMenu();
        postsToolbarMenu.findItem(R.id.menu_posts_reverse)
                        .setVisible(isAddressMarker);
        postsToolbarMenu.findItem(R.id.menu_show_more)
                        .setVisible(isAddressMarker);
    }

    private void deselectClickedMarker()
    {
        if (markerInfo != null && markerInfo.getMarkerCondition() == MarkerInfo.MARKER_CLICKED)
        {
            markerInfo.setMarkerCondition(MarkerInfo.MARKER_NOT_CLICKED);
            notifyItemChanged(markerInfoList.indexOf(markerInfo));
        }
    }

    private LatLng getLatLngByAddress(Address address)
    {
        LatLng latLng = null;
        try
        {
            Geocoder geocoder = new Geocoder(mainActivity, LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocationName(address.toString(), 1)
                            .get(0);
            latLng = new LatLng(mapAddress.getLatitude(), mapAddress.getLongitude());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return latLng;
    }

    private Address getAddressByLatLng(@NonNull LatLng latLng)
    {
        Address address = null;
        try
        {
            Geocoder geocoder = new Geocoder(mainActivity, LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            .get(0);
            address = new Address(mapAddress.getCountryName(),
                    mapAddress.getLocality(),
                    mapAddress.getThoroughfare(),
                    Integer.parseInt(mapAddress.getFeatureName()));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return address;
    }

    public void addMarker(MarkerInfo markerInfo)
    {
        Marker marker = markerInfo.getMarker();
        googleMap.addMarker(new MarkerAddition(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude).create());
        markerInfo.setMarkerCondition(MarkerInfo.MARKER_ADDED);
        markerInfoList.add(markerInfo);
        notifyItemChanged(markerInfoList.indexOf(markerInfo));
    }

    public void addMarker(MarkerOptions markerOptions,
                          int markerType)
    {
        addMarker(markerOptions, markerType, null);
    }

    public void addMarker(MarkerOptions markerOptions,
                          int markerType,
                          Map<String, Object> metadata)
    {
        Marker marker = googleMap.addMarker(markerOptions);
        MarkerInfo markerInfo = new MarkerInfo(marker, markerType);

        if (metadata != null)
            markerInfo.getMetadata()
                      .putAll(metadata);
        markerInfoList.add(markerInfo);
        markerInfo.setMarkerCondition(MarkerInfo.MARKER_ADDED);
        notifyItemChanged(markerInfoList.size() - 1);
    }

    public void removeMarkerByPostId(String postId)
    {
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfo markerInfo = markerInfoList.get(i);
            Map<String, Object> metadata = markerInfo.getMetadata();
            if (markerInfo.getMarkerType() == MarkerInfo.SUB_ADDRESS_MARKER && (String.valueOf(metadata.get("post_id"))).equals(postId))
            {
                markerInfo.setMarkerCondition(MarkerInfo.MARKER_REMOVED);
                notifyItemChanged(i);
                markerInfoList.remove(i);
                deselectClickedMarker();

                markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            }
        }
    }

    public void onDestroy()
    {
        deselectClickedMarker();
        googleMap.clear();
        markerInfoList.clear();
    }

    public static class MarkersAdapterHelper
    {
        private final BottomSheetBehavior<View> sheetBehavior;
        private final Toolbar postsToolbar;
        private final RecyclerView postsRecycler;

        public MarkersAdapterHelper(BottomSheetBehavior<View> sheetBehavior,
                                    Toolbar postsToolbar,
                                    RecyclerView postsRecycler)
        {
            this.sheetBehavior = sheetBehavior;
            this.postsToolbar = postsToolbar;
            this.postsRecycler = postsRecycler;
        }
    }
}
