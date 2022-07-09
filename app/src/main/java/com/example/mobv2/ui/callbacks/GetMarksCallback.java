package com.example.mobv2.ui.callbacks;

import android.content.Context;
import android.widget.Toast;

import com.example.mobv2.models.PostWithMark;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.example.mobv2.utils.MarkerAddition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.gson.internal.LinkedTreeMap;

public class GetMarksCallback implements MOBServerAPI.MOBAPICallback
{
    private final Context context;
    private final BitmapDescriptor markDescriptor;
    private final MainFragmentViewModel viewModel;
    private final GoogleMap googleMap;

    public GetMarksCallback(Context context,
                            BitmapDescriptor markDescriptor,
                            MainFragmentViewModel viewModel,
                            GoogleMap googleMap)
    {
        this.context = context;
        this.markDescriptor = markDescriptor;
        this.viewModel = viewModel;
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
            viewModel.addPostWithMark(postWithMark);
            googleMap.addMarker(new MarkerAddition("The mark", x, y, markDescriptor).create())
                     .setTag(false);
        }
    }

    @Override
    public void funcBad(LinkedTreeMap<String, Object> obj)
    {
        Toast.makeText(context, "There are not markers, yet", Toast.LENGTH_LONG)
             .show();
    }

    @Override
    public void fail(Throwable obj)
    {
    }
}
