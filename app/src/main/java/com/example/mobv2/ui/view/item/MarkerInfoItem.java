package com.example.mobv2.ui.view.item;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobv2.R;
import com.example.mobv2.adapter.MarkersAdapter;
import com.example.mobv2.adapter.PostsAdapter;
import com.example.mobv2.callback.GetPostCallback;
import com.example.mobv2.callback.abstraction.GetPostOkCallback;
import com.example.mobv2.model.MarkerInfoImpl;
import com.example.mobv2.model.PostImpl;
import com.example.mobv2.ui.abstraction.Item;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.view.MarkerView;
import com.example.mobv2.util.BitmapConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import localDatabase.dao.MarkerInfoDao;
import localDatabase.dao.PostDao;

public class MarkerInfoItem implements Item<MarkerView>, GetPostOkCallback
{
    private final MarkerInfoDao markerInfoDao;
    private final PostDao postDao;

    private final int[][] drawableIds =
            new int[][]{{R.drawable.ic_marker_address_24dp, R.drawable.ic_marker_address_36dp},
                    {R.drawable.ic_marker_24dp, R.drawable.ic_marker_36dp}};

    private final MainActivity mainActivity;
    private final MarkersAdapter markersAdapter;
    private final PostsAdapter postsAdapter;

    public final MarkerInfoItemHelper markerInfoItemHelper;

    public MarkerInfoItem(MainActivity mainActivity,
                          MarkersAdapter markersAdapter,
                          PostsAdapter postsAdapter,
                          MarkerInfoImpl markerInfo)
    {
        this.mainActivity = mainActivity;
        this.markersAdapter = markersAdapter;
        this.postsAdapter = postsAdapter;
        this.markerInfoItemHelper = new MarkerInfoItemHelper(markerInfo);

        markerInfoDao = mainActivity.appDatabase.markerInfoDao();
        postDao = mainActivity.appDatabase.postDao();
    }

    @Override
    public void refreshItemBinding(@NonNull MarkerView markerView)
    {
        markerView.setTitle(markerInfoItemHelper.getTitle());
        markerView.setPosition(markerInfoItemHelper.getLatLng());

        int drawableId = markerInfoItemHelper.isClicked()
                ? drawableIds[markerInfoItemHelper.getMarkerType()][1]
                : drawableIds[markerInfoItemHelper.getMarkerType()][0];

        markerView.setIcon(BitmapConverter.drawableToBitmapDescriptor(mainActivity.getResources(), drawableId));

        markerView.setOnClickListener(this::onMarkerClick);
    }

    private void onMarkerClick(MarkerView markerView)
    {
        markersAdapter.animateCameraTo(markerInfoItemHelper.getLatLng());

        if (markersAdapter.checkIfClickedMarkerInfoEqualsTo(this))
        {
            return;
        }

        markersAdapter.deselectClickedMarkerInfoItem();

        markerInfoItemHelper.setClicked(true);
        markersAdapter.refreshPostsRecycler();
        markersAdapter.refreshPostsFragment();
        markersAdapter.notifyItemChanged(this);
    }

    public void addPostToPostsThroughServer()
    {
        String postId = markerInfoItemHelper.getId();

        var callback = new GetPostCallback(mainActivity);
        callback.setOkCallback(this::parsePostFromMapAndAddToPosts);
        callback.setFailCallback(() -> getPostByIdFromLocalDbAndAddToPosts(postId));
        mainActivity.mobServerAPI.getPost(callback, postId, MainActivity.token);
    }

    @Override
    public void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map)
    {
        var post = new PostImpl.PostBuilder().parseFromMap(map);
        postDao.insert(post);
        PostItem postItem = postsAdapter.addElementAndGetItem(post);
        postItem.postItemHelper.setMarkerInfoItemHelper(markerInfoItemHelper);
        markersAdapter.scrollToStartPosition();
    }

    private void getPostByIdFromLocalDbAndAddToPosts(String postId)
    {
        var post = postDao.getById(postId);
        if (post == null)
        {
            Toast.makeText(mainActivity, "Post is not uploaded", Toast.LENGTH_LONG)
                 .show();
            return;
        }
        PostItem postItem = postsAdapter.addElementAndGetItem(post);
        postItem.postItemHelper.setMarkerInfoItemHelper(markerInfoItemHelper);
        markersAdapter.scrollToStartPosition();
    }

    public class MarkerInfoItemHelper
    {
        private final MarkerInfoImpl markerInfo;

        public MarkerInfoItemHelper(MarkerInfoImpl markerInfo)
        {
            this.markerInfo = markerInfo;
        }

        public boolean compareById(MarkerInfoItemHelper markerInfoItemHelper)
        {
            return this.markerInfo.compareById(markerInfoItemHelper.markerInfo);
        }

        public void goTo()
        {
            markersAdapter.animateCameraTo(getLatLng());
        }

        public void delete()
        {
            markersAdapter.deleteMarkerInfoItem(MarkerInfoItem.this);
            markerInfoDao.delete(markerInfo);
        }

        public String getId()
        {
            return markerInfo.getId();
        }

        public String getTitle()
        {
            return markerInfo.getTitle();
        }

        public LatLng getLatLng()
        {
            var latLng = markerInfo.getLatLng();
            return latLng == null ? new LatLng(0, 0) : latLng;
        }

        public int getMarkerType()
        {
            return markerInfo.getMarkerType();
        }

        public boolean isClicked()
        {
            return markerInfo.isClicked();
        }

        public void setClicked(boolean clicked)
        {
            markerInfo.setClicked(clicked);
        }
    }
}
