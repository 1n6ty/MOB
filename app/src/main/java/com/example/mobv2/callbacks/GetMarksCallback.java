package com.example.mobv2.callbacks;

import android.util.Log;

import com.example.mobv2.adapters.MapAdapter;
import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;

public class GetMarksCallback implements MOBServerAPI.MOBAPICallback
{
    private final MainActivity mainActivity;
    private final MapAdapter mapAdapter;

    public GetMarksCallback(MainActivity mainActivity,
                            MapAdapter mapAdapter)
    {
        this.mainActivity = mainActivity;
        this.mapAdapter = mapAdapter;
    }

    @Override
    public void funcOk(LinkedTreeMap<String, Object> obj)
    {
        var response = (LinkedTreeMap<String, Object>) obj.get("response");

        for (var postId : response.keySet())
        {
            var postWithMarkMap = (LinkedTreeMap<String, Object>) response.get(postId);
            var mark = (LinkedTreeMap<String, Double>) postWithMarkMap.get("mark");
            double x = mark.get("x");
            double y = mark.get("y");
            MarkerInfo markerInfo =
                    new MarkerInfo("The mark", new LatLng(x, y), MarkerInfo.SUB_ADDRESS_MARKER);
            markerInfo.getMetadata().put("post_id", Integer.valueOf(postId));
            mapAdapter.addMarker(markerInfo);
        }
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
}
