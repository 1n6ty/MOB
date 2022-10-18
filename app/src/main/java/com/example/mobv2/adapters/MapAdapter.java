package com.example.mobv2.adapters;

import android.location.Geocoder;
import android.util.Log;
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
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.models.abstractions.Address;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.markercreators.MapSkillsBottomSheetFragment;
import com.example.mobv2.ui.fragments.markercreators.MarkerCreatorViewModel;
import com.example.mobv2.utils.BitmapConverter;
import com.example.mobv2.utils.MapView;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import localdatabase.daos.AddressDao;
import localdatabase.daos.PostDao;
import serverapi.MOBServerAPI;

public class MapAdapter extends MapView.Adapter
{
    public static final int ZOOM = 16;
    private final static Locale LOCALE = Locale.ENGLISH;

    private final AddressDao addressDao;
    private final PostDao postDao;

    private final MainActivity mainActivity;
    private final List<MarkerInfo> markerInfoList;
    private final MarkersAdapterHelper markersAdapterHelper;
    private final int[][] drawableIds;
    private MapView mapView;
    private MarkerInfo bufferMarkerInfo = new MarkerInfo();

    public MapAdapter(MainActivity mainActivity,
                      MarkersAdapterHelper markersAdapterHelper)
    {
        this.mainActivity = mainActivity;
        this.markerInfoList = new ArrayList<>();
        this.markersAdapterHelper = markersAdapterHelper;

        drawableIds =
                new int[][]{{R.drawable.ic_marker_address_24dp, R.drawable.ic_marker_address_36dp},
                        {R.drawable.ic_marker_24dp, R.drawable.ic_marker_36dp}};

        addressDao = mainActivity.appDatabase.addressDao();
        postDao = mainActivity.appDatabase.postDao();
    }

    @Override
    public void onCreate(MapView mapView)
    {
        this.mapView = mapView;

        setGoogleMapListeners();
        setPostsToolbarListeners();

        AddressImpl currentAddress = addressDao.getCurrent();
        if (currentAddress != null)
        {
            String addressString = currentAddress.toString();

            if (addressString.isEmpty()) return;

            LatLng addressLatLng =
                    getLatLngByAddress(new AddressImpl.AddressBuilder().parseFromString(addressString));
            // add address marker
            addMarkerInfo(new MarkerInfo(addressString, addressLatLng, MarkerInfo.ADDRESS_MARKER));

            // add other markers
            mainActivity.mobServerAPI.getMarks(new GetMarksCallback(mainActivity, this::parseMarkersFromMapAndAddToMarkerInfoList), MainActivity.token);
            animateCameraTo(addressLatLng);
        }
    }

    private void parseMarkersFromMapAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map)
    {
        for (var postId : map.keySet())
        {
            var markerMap = (LinkedTreeMap<String, Object>) map.get(postId);
            var markerInfo = new MarkerInfo.MarkerInfoBuilder().parseFromMap(markerMap);
            markerInfo.getMetadata()
                      .put("post_id", Integer.valueOf(postId));

            addMarkerInfo(markerInfo);
        }
    }

    private void setGoogleMapListeners()
    {
        mapView.setOnMarkerClickListener(this::onMarkerClick);
        mapView.setOnMapClickListener(latLng -> onMapClick());
        mapView.setOnMapLongClickListener(this::onMapLongClick);
    }

    private void setPostsToolbarListeners()
    {
        Toolbar postsToolbar = markersAdapterHelper.postsToolbar;
        postsToolbar.setNavigationOnClickListener(view -> onMapClick());
        postsToolbar.getMenu()
                    .findItem(R.id.menu_posts_refresh)
                    .setOnMenuItemClickListener(view ->
                    {
                        refreshPostsRecycler();
                        return true;
                    });
    }

    @Override
    public void onBindMarker(Marker marker,
                             int position)
    {
        MarkerInfo markerInfo = markerInfoList.get(position);

        markerInfo.setId(marker.getId());

        marker.setTitle(markerInfo.getTitle());
        marker.setPosition(markerInfo.getPosition());
        marker.setTag(markerInfo.getTag());

        int drawableId = markerInfo.isClicked()
                ? drawableIds[markerInfo.getMarkerType()][1]
                : drawableIds[markerInfo.getMarkerType()][0];

        marker.setIcon(BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId));
    }

    private void onMapClick()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        deselectClickedMarkerInfo();
    }

    private void onMapLongClick(@NonNull LatLng latLng)
    {
        animateCameraTo(latLng);

        initMarkerCreatorViewModel(latLng);

        Marker pointerMarker =
                mapView.addMarker(new MarkerAddition(latLng.latitude, latLng.longitude).create());

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
                Log.v("DEBUG", obj.toString());

                var response = (LinkedTreeMap<String, Object>) obj.get("response");

                int postId = ((Double) response.get("id")).intValue();
                double x = latLng.latitude;
                double y = latLng.longitude;
                MarkerInfo markerInfo =
                        new MarkerInfo(new LatLng(x, y), MarkerInfo.SUB_ADDRESS_MARKER);
                markerInfo.getMetadata()
                          .put("post_id", postId);
                markerInfoList.add(markerInfo);
                notifyItemInserted(markerInfoList.size() - 1);
            }

            @Override
            public void funcBad(LinkedTreeMap<String, Object> obj)
            {
                Log.v("DEBUG", obj.toString());
            }

            @Override
            public void fail(Throwable obj)
            {
                Log.v("DEBUG", obj.toString());
            }
        });

        Address address = getAddressByLatLng(latLng);

        markerCreatorViewModel.setAddress(address);
    }

    @Deprecated
    private Address getAddressByLatLng(@NonNull LatLng latLng)
    {
        Address address = null;
        try
        {
            Geocoder geocoder = new Geocoder(mainActivity, LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            .get(0);
            address =
                    AddressImpl.createRawAddress(mapAddress.getCountryName(), mapAddress.getLocality(), mapAddress.getThoroughfare(), mapAddress.getFeatureName());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return address;
    }

    private void animateCameraTo(@NonNull LatLng latLng)
    {
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
    }

    private void onMarkerClick(int position)
    {
        MarkerInfo markerInfo = markerInfoList.get(position);

        if (checkIfMarkerInfoEqualsClickedMarkerInfo(markerInfo)) return;

        deselectClickedMarkerInfo();

        animateCameraTo(markerInfo.getPosition());

        bufferMarkerInfo = markerInfo;
        markerInfo.setClicked(true);
        refreshPostsRecycler();
        refreshPosts();
        notifyItemChanged(position);
    }

    private void deselectClickedMarkerInfo()
    {
        MarkerInfo clickedMarkerInfo = getClickedMarkerInfo();
        clickedMarkerInfo.setClicked(false);
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfo markerInfo = markerInfoList.get(i);
            if (markerInfo.compareById(clickedMarkerInfo))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private MarkerInfo getClickedMarkerInfo()
    {
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfo markerInfo = markerInfoList.get(i);
            if (markerInfo.isClicked()) return markerInfo;
        }

        return new MarkerInfo();
    }

    private boolean checkIfMarkerInfoEqualsClickedMarkerInfo(MarkerInfo markerInfo)
    {
        return markerInfo.compareById(getClickedMarkerInfo());
    }

    private void refreshPostsRecycler()
    {
        markersAdapterHelper.postsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        var postsAdapter = new PostsAdapter(mainActivity, this::removeMarkerByPostId);
        markersAdapterHelper.postsRecyclerView.setAdapter(postsAdapter);

        switch (bufferMarkerInfo.getMarkerType())
        {
            case MarkerInfo.ADDRESS_MARKER:
                for (var markerInfo : markerInfoList)
                {
                    if (markerInfo.getMarkerType() == MarkerInfo.SUB_ADDRESS_MARKER)
                    {
                        addPostToPostsThroughServerFromMarkerInfoMetadata(markerInfo);
                    }
                }
                break;
            case MarkerInfo.SUB_ADDRESS_MARKER:
                addPostToPostsThroughServerFromMarkerInfoMetadata(bufferMarkerInfo);
                break;
        }
    }

    private void addPostToPostsThroughServerFromMarkerInfoMetadata(MarkerInfo markerInfo)
    {
        String postId = String.valueOf(markerInfo.getMetadata()
                                                 .get("post_id"));

        mainActivity.mobServerAPI.getPost(new GetPostCallback(mainActivity,
                this::parsePostFromMapAndAddToPosts,
                () -> markersAdapterHelper.getPostsAdapter()
                                          .addElement(postDao.getById(postId))), postId, MainActivity.token);
    }

    private void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map)
    {
        PostImpl post = new PostImpl.PostBuilder().parseFromMap(map);
        postDao.insert(post);

        markersAdapterHelper.getPostsAdapter()
                            .addElement(post);
    }

    private void refreshPosts()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        final boolean isAddressMarker =
                bufferMarkerInfo.getMarkerType() == MarkerInfo.ADDRESS_MARKER;
        Menu postsToolbarMenu = markersAdapterHelper.postsToolbar.getMenu();
        postsToolbarMenu.findItem(R.id.menu_posts_reverse)
                        .setVisible(isAddressMarker);
        postsToolbarMenu.findItem(R.id.menu_show_more)
                        .setVisible(isAddressMarker);
    }

    @Deprecated
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

    public void addMarkerInfo(MarkerInfo markerInfo)
    {
        markerInfoList.add(markerInfo);
        notifyItemInserted(markerInfoList.size() - 1);
    }

    private void removeMarkerByPostId(String postId)
    {
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfo markerInfo = markerInfoList.get(i);
            Map<String, Object> metadata = markerInfo.getMetadata();
            if (markerInfo.getMarkerType() == MarkerInfo.SUB_ADDRESS_MARKER && (String.valueOf(metadata.get("post_id"))).equals(postId))
            {
                markerInfoList.remove(i);
                notifyItemRemoved(i);

                markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            }
        }
    }

    public void onDestroy()
    {
        deselectClickedMarkerInfo();
        mapView.clear();
        markerInfoList.clear();
    }

    @Override
    public int getItemCount()
    {
        return markerInfoList.size();
    }

    public interface MapAdapterCallback
    {
        void removeMarkerByPostId(String postId);
    }

    public interface MarkersOkCallback
    {
        void parseMarkersFromMapAndAddToMarkers(LinkedTreeMap<String, Object> map);
    }

    public interface PostOkCallback
    {
        void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map);
    }

    public interface PostFailCallback
    {
        void onDisconnect();
    }

    public static class MarkersAdapterHelper
    {
        private final BottomSheetBehavior<View> sheetBehavior;
        private final Toolbar postsToolbar;
        private final RecyclerView postsRecyclerView;

        public MarkersAdapterHelper(BottomSheetBehavior<View> sheetBehavior,
                                    Toolbar postsToolbar,
                                    RecyclerView postsRecyclerView)
        {
            this.sheetBehavior = sheetBehavior;
            this.postsToolbar = postsToolbar;
            this.postsRecyclerView = postsRecyclerView;
        }

        public PostsAdapter getPostsAdapter()
        {
            return (PostsAdapter) postsRecyclerView.getAdapter();
        }
    }
}
