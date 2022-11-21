package com.example.mobv2.adapter;

import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.abstraction.AbleToAdd;
import com.example.mobv2.callback.CreatePostCallback;
import com.example.mobv2.callback.abstraction.CreatePostOkCallback;
import com.example.mobv2.model.MarkerInfoImpl;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.markerCreator.MapSkillsBottomSheetFragment;
import com.example.mobv2.ui.fragment.markerCreator.MarkerCreatorViewModel;
import com.example.mobv2.ui.view.MapView;
import com.example.mobv2.ui.view.MarkerView;
import com.example.mobv2.ui.view.item.MarkerInfoItem;
import com.example.mobv2.util.MarkerAddition;
import com.example.mobv2.util.MyObservableArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Locale;
import java.util.Objects;

public class MarkersAdapter extends MapView.Adapter implements AbleToAdd<MarkerInfoImpl>, CreatePostOkCallback
{
    public static final int ZOOM = 18;
    public static final int MIN_ZOOM = 16;
    public static final int CIRCLE_RADIUS = 50;  // in meters
    public static final Locale LOCALE = Locale.ENGLISH;

    private final MainActivity mainActivity;
    private final MyObservableArrayList<MarkerInfoItem> markerInfoItemList;
    private final MarkersAdapterHelper markersAdapterHelper;
    private MapView mapView;
//    private MarkerInfoItem bufferMarkerInfoItem;

    public MarkersAdapter(MainActivity mainActivity,
                          MarkersAdapterHelper markersAdapterHelper)
    {
        this.mainActivity = mainActivity;
        this.markersAdapterHelper = markersAdapterHelper;

        markerInfoItemList = new MyObservableArrayList<>();
        markerInfoItemList.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(int index,
                                MarkerInfoItem element)
            {
                notifyItemInserted(index);
            }

            @Override
            public void onRemoved(int index)
            {
                notifyItemRemoved(index);
            }

            @Override
            public void onRemoved(int index,
                                  Object o)
            {
                notifyItemRemoved(index);
            }

            @Override
            public void onClear(int count)
            {
                notifyItemRangeRemoved(0, count);
            }
        });
    }

    @Override
    public void onCreate(MapView mapView)
    {
        this.mapView = mapView;

        setGoogleMapListeners();
    }

    private void setGoogleMapListeners()
    {
        mapView.setOnMapClickListener(latLng -> onMapClick());
        mapView.setOnMapLongClickListener(this::onMapLongClick);
        mapView.setOnCameraMoveListener(() ->
        {
            if (mapView.getCameraPosition().zoom < MIN_ZOOM)
            {
                mapView.hideMarkers();
            }
            else
            {
                mapView.showMarkers();
            }
        });
    }

    public void onMapClick()
    {
        deselectClickedMarkerInfoAndHideBottom();
    }

    private void onMapLongClick(@NonNull LatLng latLng)
    {
        var currentAddress = mainActivity.appDatabase.addressDao()
                                                     .getCurrentOne();

        var currentAddressLatLng = currentAddress.getLatLng();
        var distance =
                mainActivity.getDistance(latLng.latitude, latLng.longitude, currentAddressLatLng.latitude, currentAddressLatLng.longitude);

        if (distance[0] > MarkersAdapter.CIRCLE_RADIUS) return;

        deselectClickedMarkerInfoAndHideBottom();

        initMarkerCreatorViewModel(latLng);

        var pointerMarker =
                mapView.addMarker(new MarkerAddition(latLng.latitude, latLng.longitude).create());

        animateCameraTo(latLng, new GoogleMap.CancelableCallback()
        {
            @Override
            public void onCancel()
            {
                Objects.requireNonNull(pointerMarker)
                       .remove();
            }

            @Override
            public void onFinish()
            {
                var bottomSheetFragment = new MapSkillsBottomSheetFragment();
                bottomSheetFragment.setOnDestroyViewListener(view -> Objects.requireNonNull(pointerMarker)
                                                                            .remove());
                bottomSheetFragment.show(mainActivity.getSupportFragmentManager(), MapSkillsBottomSheetFragment.class.getSimpleName());
            }
        });
    }

    private void initMarkerCreatorViewModel(@NonNull LatLng latLng)
    {
        var markerCreatorViewModel =
                new ViewModelProvider(mainActivity).get(MarkerCreatorViewModel.class);

        var createPostCallback = new CreatePostCallback(mainActivity, latLng);
        createPostCallback.setOkCallback(this::parseMarkerInfoFromMapWithLatLngAndAddToMarkerInfoList);
        markerCreatorViewModel.setCreatePostCallback(createPostCallback);

        var address = mainActivity.getOtherAddressByLatLng(latLng);

        markerCreatorViewModel.setAddress(address);
    }

    @Override
    public void parseMarkerInfoFromMapWithLatLngAndAddToMarkerInfoList(@NonNull LinkedTreeMap<String, Object> map,
                                                                       @NonNull LatLng latLng)
    {
        String postId = String.valueOf(((Double) map.get("id")).intValue());
        double x = latLng.latitude;
        double y = latLng.longitude;
        var markerInfo = new MarkerInfoImpl(postId, new LatLng(x, y), MarkerInfoImpl.SUB_ADDRESS_MARKER);

        addElement(markerInfo);
    }

    @Override
    public void onBindMarkerView(MarkerView markerView,
                                 int position)
    {
        var markerInfoItem = markerInfoItemList.get(position);

        markerInfoItem.refreshItemBinding(markerView);
    }

    @Override
    public void addElement(@NonNull MarkerInfoImpl markerInfo)
    {
        var markerInfoItem =
                new MarkerInfoItem(mainActivity, this, markersAdapterHelper.postsRecyclerView, markerInfo);
        markerInfoItemList.add(markerInfoItem);
    }

    public void refreshPostsRecycler()
    {
        var postsAdapter = new PostsAdapter(mainActivity, this);
        markersAdapterHelper.postsRecyclerView.setAdapter(postsAdapter);
        markersAdapterHelper.postsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        var clickedMarkerInfoItem = getClickedMarkerInfoItem();
        switch (clickedMarkerInfoItem.markerInfoItemHelper.getMarkerType())
        {
            case MarkerInfoImpl.ADDRESS_MARKER:
                for (var markerInfoItem : markerInfoItemList)
                {
                    if (markerInfoItem.markerInfoItemHelper.getMarkerType() == MarkerInfoImpl.SUB_ADDRESS_MARKER)
                    {
                        markerInfoItem.addPostToPostsThroughServer();
                    }
                }
                break;
            case MarkerInfoImpl.SUB_ADDRESS_MARKER:
                clickedMarkerInfoItem.addPostToPostsThroughServer();
                break;
        }
    }

    public void refreshPostsFragment()
    {
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        final boolean isAddressMarker =
                getClickedMarkerInfoItem().markerInfoItemHelper.getMarkerType() == MarkerInfoImpl.ADDRESS_MARKER;
        Menu postsToolbarMenu = markersAdapterHelper.postsToolbar.getMenu();
        postsToolbarMenu.findItem(R.id.menu_posts_reverse)
                        .setVisible(isAddressMarker);
        postsToolbarMenu.findItem(R.id.menu_show_more)
                        .setVisible(isAddressMarker);
    }

    public void deleteMarkerInfoItem(MarkerInfoItem markerInfoItem)
    {
        markerInfoItemList.remove(markerInfoItem);
    }

    private void deselectClickedMarkerInfoAndHideBottom()
    {
        deselectClickedMarkerInfoItem();
        markersAdapterHelper.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void onDestroy()
    {
        deselectClickedMarkerInfoItem();
        markerInfoItemList.clear();
    }

    public void deselectClickedMarkerInfoItem()
    {
        var clickedMarkerInfo = getClickedMarkerInfoItem().markerInfoItemHelper;
        clickedMarkerInfo.setClicked(false);
        for (int i = 0; i < markerInfoItemList.size(); i++)
        {
            var markerInfo = markerInfoItemList.get(i).markerInfoItemHelper;
            if (markerInfo.compareById(clickedMarkerInfo))
            {
                notifyItemChanged(i);
                return;
            }
        }
    }

    public boolean checkIfClickedMarkerInfoEqualsTo(MarkerInfoItem markerInfoItem)
    {
        return markerInfoItem.markerInfoItemHelper.compareById(getClickedMarkerInfoItem().markerInfoItemHelper);
    }

    private MarkerInfoItem getClickedMarkerInfoItem()
    {
        for (int i = 0; i < markerInfoItemList.size(); i++)
        {
            var markerInfoItem = markerInfoItemList.get(i);
            if (markerInfoItem.markerInfoItemHelper.isClicked())
            {
                return markerInfoItem;
            }
        }

        return new MarkerInfoItem(mainActivity, this, null, new MarkerInfoImpl());
    }

    public void animateCameraTo(@NonNull LatLng latLng)
    {
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Math.max(ZOOM, mapView.getCameraPosition().zoom)));
    }

    public void animateCameraTo(@NonNull LatLng latLng,
                                GoogleMap.CancelableCallback callback)
    {
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Math.max(ZOOM, mapView.getCameraPosition().zoom)), callback);
    }

    public void scrollToStartPosition()
    {
        markersAdapterHelper.postsRecyclerView.scrollToPosition(0);
    }

    public void notifyItemChanged(MarkerInfoItem markerInfoItem)
    {
        notifyItemChanged(markerInfoItemList.indexOf(markerInfoItem));
    }

    @Override
    public int getItemCount()
    {
        return markerInfoItemList.size();
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
    }
}
