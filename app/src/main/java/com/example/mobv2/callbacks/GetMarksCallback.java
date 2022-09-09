package com.example.mobv2.callbacks;

import com.example.mobv2.adapters.MapAdapter;
import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.utils.MarkerAddition;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

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
        LinkedTreeMap<String, LinkedTreeMap<String, Double>> response =
                (LinkedTreeMap<String, LinkedTreeMap<String, Double>>) obj.get("response");

        for (String postId : response.keySet())
        {
            LinkedTreeMap<String, Double> coordinates = response.get(postId);
            double x = coordinates.get("x");
            double y = coordinates.get("y");
            HashMap<String, Object> metadata = new HashMap<>();
            metadata.put("post_id", Integer.valueOf(postId));
            mapAdapter.addMarker(new MarkerAddition("The mark", x, y).create(), MarkerInfo.SUB_ADDRESS_MARKER, metadata);
        }
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
    }

    @Override
    public void fail(Throwable obj)
    {
    }
}
