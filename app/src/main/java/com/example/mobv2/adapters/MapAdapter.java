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
import com.example.mobv2.adapters.abstractions.Addable;
import com.example.mobv2.callbacks.CreatePostCallback;
import com.example.mobv2.callbacks.GetMarkersCallback;
import com.example.mobv2.callbacks.GetPostCallback;
import com.example.mobv2.callbacks.abstractions.CreatePostOkCallback;
import com.example.mobv2.callbacks.abstractions.GetMarkersOkCallback;
import com.example.mobv2.callbacks.abstractions.GetPostOkCallback;
import com.example.mobv2.callbacks.abstractions.MapAdapterCallback;
import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.MarkerInfoImpl;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.models.abstractions.Address;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.callbacks.FinishableCallback;
import com.example.mobv2.ui.fragments.markercreators.MapSkillsBottomSheetFragment;
import com.example.mobv2.ui.fragments.markercreators.MarkerCreatorViewModel;
import com.example.mobv2.utils.BitmapConverter;
import com.example.mobv2.utils.MapView;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import localdatabase.daos.MarkerInfoDao;
import localdatabase.daos.PostDao;

public class MapAdapter extends MapView.Adapter implements Addable<MarkerInfoImpl>, MapAdapterCallback, CreatePostOkCallback, GetPostOkCallback, GetMarkersOkCallback
{
    public static final int ZOOM = 16;
    public static final Locale LOCALE = Locale.ENGLISH;

    private final AddressDao addressDao;
    private final MarkerInfoDao markerInfoDao;
    private final PostDao postDao;

    private final MainActivity mainActivity;
    private final List<MarkerInfoImpl> markerInfoList;
    private final MarkersAdapterHelper markersAdapterHelper;
    private final int[][] drawableIds;
    private MapView mapView;
    private MarkerInfoImpl bufferMarkerInfo = new MarkerInfoImpl();

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
        markerInfoDao = mainActivity.appDatabase.markerInfoDao();
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
            var addressString = currentAddress.toString();

            if (addressString.isEmpty()) return;

            // add address marker
            addElement(new MarkerInfoImpl(addressString, currentAddress.getPosition(), MarkerInfoImpl.ADDRESS_MARKER));

            // add other markers
            var callback = new GetMarkersCallback(mainActivity);
            callback.setOkCallback(this::parseMarkersFromMapAndAddToMarkerInfoList);

            mainActivity.mobServerAPI.getMarks(callback, MainActivity.token);
            animateCameraTo(currentAddress.getPosition());
        }
    }

    private void setGoogleMapListeners()
    {
        mapView.setOnMarkerClickListener(this::onMarkerClick);
        mapView.setOnMapClickListener(latLng -> onMapClick());
        mapView.setOnMapLongClickListener(this::onMapLongClick);
    }

    private void onMarkerClick(int position)
    {
        MarkerInfoImpl markerInfo = markerInfoList.get(position);

        if (checkIfMarkerInfoEqualsClickedMarkerInfo(markerInfo)) return;

        deselectClickedMarkerInfo();

        animateCameraTo(markerInfo.getPosition());

        bufferMarkerInfo = markerInfo;
        markerInfo.setClicked(true);
        refreshPostsRecycler();
        refreshPosts();
        notifyItemChanged(position);
    }

    private void onMapLongClick(@NonNull LatLng latLng)
    {
        initMarkerCreatorViewModel(latLng);

        var pointerMarker =
                mapView.addMarker(new MarkerAddition(latLng.latitude, latLng.longitude).create());

        var bottomSheetFragment = new MapSkillsBottomSheetFragment();
        bottomSheetFragment.setOnDestroyViewListener(view -> pointerMarker.remove());

        animateCameraTo(latLng, new FinishableCallback()
        {
            @Override
            public void onFinish()
            {
                bottomSheetFragment.show(mainActivity.getSupportFragmentManager(), MapSkillsBottomSheetFragment.class.getSimpleName());
            }
        });

    }

    private void initMarkerCreatorViewModel(@NonNull LatLng latLng)
    {
        var markerCreatorViewModel =
                new ViewModelProvider(mainActivity).get(MarkerCreatorViewModel.class);

        markerCreatorViewModel.setLatLng(latLng);
        var createPostCallback = new CreatePostCallback(mainActivity, latLng);
        createPostCallback.setOkCallback(this::parsePostIdFromMapAndAddUsingPositionToMarkerInfoList);
        markerCreatorViewModel.setCallback(createPostCallback);

        Address address = getOtherAddressByLatLng(latLng);

        markerCreatorViewModel.setAddress(address);
    }

    @Override
    public void parsePostIdFromMapAndAddUsingPositionToMarkerInfoList(LinkedTreeMap<String, Object> map,
                                                                      LatLng position)
    {
        int postId = ((Double) map.get("id")).intValue();
        double x = position.latitude;
        double y = position.longitude;
        var markerInfo = new MarkerInfoImpl(new LatLng(x, y), MarkerInfoImpl.SUB_ADDRESS_MARKER);
        markerInfo.getMetadata()
                  .put("post_id", postId);

        addElement(markerInfo);
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
    public void parseMarkersFromMapAndAddToMarkerInfoList(LinkedTreeMap<String, Object> map)
    {
        for (var postId : map.keySet())
        {
            var markerMap = (LinkedTreeMap<String, Object>) map.get(postId);
            var markerInfo = new MarkerInfoImpl.MarkerInfoBuilder().parseFromMap(markerMap);
            markerInfo.getMetadata()
                      .put("post_id", Integer.valueOf(postId));

            addElement(markerInfo);
        }
    }

    @Override
    public void onBindMarker(Marker marker,
                             int position)
    {
        MarkerInfoImpl markerInfo = markerInfoList.get(position);

        markerInfo.setId(marker.getId());

        marker.setTitle(markerInfo.getTitle());
        marker.setPosition(markerInfo.getPosition());

        int drawableId = markerInfo.isClicked()
                ? drawableIds[markerInfo.getMarkerType()][1]
                : drawableIds[markerInfo.getMarkerType()][0];

        marker.setIcon(BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId));
    }

    public void onMapClick()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        deselectClickedMarkerInfo();
    }

    private Address getOtherAddressByLatLng(@NonNull LatLng latLng)
    {
        try
        {
            Geocoder geocoder = new Geocoder(mainActivity, LOCALE);
            android.location.Address mapAddress =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            .get(0);

            return AddressImpl.createRawAddress(mapAddress.getCountryName(), mapAddress.getLocality(), mapAddress.getThoroughfare(), mapAddress.getFeatureName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void animateCameraTo(@NonNull LatLng latLng)
    {
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
    }

    public void animateCameraTo(@NonNull LatLng latLng, GoogleMap.CancelableCallback callback)
    {
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM), callback);
    }

    private void deselectClickedMarkerInfo()
    {
        MarkerInfoImpl clickedMarkerInfo = getClickedMarkerInfo();
        clickedMarkerInfo.setClicked(false);
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfoImpl markerInfo = markerInfoList.get(i);
            if (markerInfo.compareById(clickedMarkerInfo))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private MarkerInfoImpl getClickedMarkerInfo()
    {
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfoImpl markerInfo = markerInfoList.get(i);
            if (markerInfo.isClicked()) return markerInfo;
        }

        return new MarkerInfoImpl();
    }

    private boolean checkIfMarkerInfoEqualsClickedMarkerInfo(MarkerInfoImpl markerInfo)
    {
        return markerInfo.compareById(getClickedMarkerInfo());
    }

    public void refreshPostsRecycler()
    {
        markersAdapterHelper.postsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        var postsAdapter = new PostsAdapter(mainActivity, this::removeMarkerByPostId);
        markersAdapterHelper.postsRecyclerView.setAdapter(postsAdapter);

        switch (bufferMarkerInfo.getMarkerType())
        {
            case MarkerInfoImpl.ADDRESS_MARKER:
                for (var markerInfo : markerInfoList)
                {
                    if (markerInfo.getMarkerType() == MarkerInfoImpl.SUB_ADDRESS_MARKER)
                    {
                        addPostToPostsThroughServerFromMarkerInfoMetadata(markerInfo);
                    }
                }
                break;
            case MarkerInfoImpl.SUB_ADDRESS_MARKER:
                addPostToPostsThroughServerFromMarkerInfoMetadata(bufferMarkerInfo);
                break;
        }
    }

    private void addPostToPostsThroughServerFromMarkerInfoMetadata(MarkerInfoImpl markerInfo)
    {
        String postId = String.valueOf(markerInfo.getMetadata()
                                                 .get("post_id"));

        var callback = new GetPostCallback(mainActivity);
        callback.setOkCallback(this::parsePostFromMapAndAddToPosts);
        callback.setFailCallback(() ->
        {
            markersAdapterHelper.getPostsAdapter()
                                .addElement(postDao.getById(postId));
            markersAdapterHelper.postsRecyclerView.scrollToPosition(0);
        });
        mainActivity.mobServerAPI.getPost(callback, postId, MainActivity.token);
    }

    @Override
    public void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map)
    {
        PostImpl post = new PostImpl.PostBuilder().parseFromMap(map);
        postDao.insert(post);

        markersAdapterHelper.getPostsAdapter()
                            .addElement(post);

        markersAdapterHelper.postsRecyclerView.scrollToPosition(0);
    }

    private void refreshPosts()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        final boolean isAddressMarker =
                bufferMarkerInfo.getMarkerType() == MarkerInfoImpl.ADDRESS_MARKER;
        Menu postsToolbarMenu = markersAdapterHelper.postsToolbar.getMenu();
        postsToolbarMenu.findItem(R.id.menu_posts_reverse)
                        .setVisible(isAddressMarker);
        postsToolbarMenu.findItem(R.id.menu_show_more)
                        .setVisible(isAddressMarker);
    }

    @Override
    public void addElement(@NonNull MarkerInfoImpl markerInfo)
    {
        markerInfoList.add(markerInfo);
        notifyItemInserted(markerInfoList.size() - 1);
    }

    @Override
    public void removeMarkerByPostId(String postId)
    {
        for (int i = 0; i < markerInfoList.size(); i++)
        {
            MarkerInfoImpl markerInfo = markerInfoList.get(i);
            Map<String, Object> metadata = markerInfo.getMetadata();
            if (markerInfo.getMarkerType() == MarkerInfoImpl.SUB_ADDRESS_MARKER && (String.valueOf(metadata.get("post_id"))).equals(postId))
            {
                markerInfoList.remove(markerInfo);
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
