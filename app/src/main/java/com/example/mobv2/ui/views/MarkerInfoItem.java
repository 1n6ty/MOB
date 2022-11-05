package com.example.mobv2.ui.views;

import androidx.annotation.NonNull;

import com.example.mobv2.R;
import com.example.mobv2.adapters.MarkersAdapter;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.callbacks.GetPostCallback;
import com.example.mobv2.callbacks.abstractions.GetPostOkCallback;
import com.example.mobv2.models.MarkerInfoImpl;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.ui.abstractions.Item;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.utils.BitmapConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import localdatabase.daos.MarkerInfoDao;
import localdatabase.daos.PostDao;

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
        markerInfoItemHelper.setId(markerView.getId());

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
        markersAdapter.setBufferMarkerInfoItem(this);
        markersAdapter.refreshPostsRecycler();
        markersAdapter.refreshPostsFragment();
        refreshItemBinding(markerView);
    }

    public void addPostToPostsThroughServer()
    {
        String postId = markerInfoItemHelper.getPostIds()
                                            .get(0);

        var callback = new GetPostCallback(mainActivity);
        callback.setOkCallback(this::parsePostFromMapAndAddToPosts);
        callback.setFailCallback(() -> getPostFromLocalDbAndAddToPosts(postId));
        mainActivity.mobServerAPI.getPost(callback, postId, MainActivity.token);
    }

    @Override
    public void parsePostFromMapAndAddToPosts(LinkedTreeMap<String, Object> map)
    {
        var post = new PostImpl.PostBuilder().parseFromMap(map);
        postDao.insert(post);
        PostItem postItem = postsAdapter.addElementAndGetItem(post);
        postItem.postItemHelper.setMarkerInfoItemHelper(markerInfoItemHelper);
//        markersAdapterHelper.postsRecyclerView.scrollToPosition(0);
    }

    public void getPostFromLocalDbAndAddToPosts(String postId)
    {
        var post = postDao.getById(postId);
        PostItem postItem = postsAdapter.addElementAndGetItem(post);
        postItem.postItemHelper.setMarkerInfoItemHelper(markerInfoItemHelper);
//            markersAdapterHelper.postsRecyclerView.scrollToPosition(0);
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
        }

        public String getId()
        {
            return markerInfo.getId();
        }

        public void setId(String id)
        {
            markerInfo.setId(id);
        }

        public String getTitle()
        {
            return markerInfo.getTitle();
        }

        public LatLng getLatLng()
        {
            return markerInfo.getLatLng();
        }

        public int getMarkerType()
        {
            return markerInfo.getMarkerType();
        }

        public List<String> getPostIds()
        {
            return markerInfo.getPostIds();
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
