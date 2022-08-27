package com.example.mobv2.callbacks;

import android.content.Context;

import com.example.mobv2.models.MarkerInfo;
import com.example.mobv2.models.PostWithMark;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class GetMarksCallback implements MOBServerAPI.MOBAPICallback
{
    private final Context context;
    private final BitmapDescriptor markDescriptor;
    private final List<PostWithMark> postsWithMarks;
    private final GoogleMap googleMap;

    public GetMarksCallback(Context context,
                            BitmapDescriptor markDescriptor,
                            List<PostWithMark> postsWithMarks,
                            GoogleMap googleMap)
    {
        this.context = context;
        this.markDescriptor = markDescriptor;
        this.postsWithMarks = postsWithMarks;
        this.googleMap = googleMap;
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
            PostWithMark postWithMark = new PostWithMark(x, y, Integer.parseInt(postId));
            postsWithMarks.add(postWithMark);
            googleMap.addMarker(new MarkerAddition("The mark", x, y, markDescriptor).create())
                     .setTag(MarkerInfo.COMMON_MARKER);
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
