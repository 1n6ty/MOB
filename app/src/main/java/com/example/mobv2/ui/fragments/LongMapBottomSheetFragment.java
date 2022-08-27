package com.example.mobv2.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapters.ImagesAdapter;
import com.example.mobv2.databinding.FragmentBottomSheetLongMapBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.serverapi.MOBServerAPI;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class LongMapBottomSheetFragment extends BottomSheetDialogFragment
{
    private FragmentBottomSheetLongMapBinding binding;

    private final ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), this::onActivityResult);
    private final LatLng latLng;
    private MOBServerAPI.MOBAPICallback callback;
    private final List<String> imagePaths;

    private TextView coordinatesView;
    private TextView addressView;
    private EditText markerTitleView;
    private Button addMarkerButton;
    private RecyclerView imagesView;
    private ImageView addImageButton;

    public LongMapBottomSheetFragment(LatLng latLng)
    {
        this.latLng = latLng;

        this.imagePaths = new ArrayList<>();
    }

    public void setCallback(MOBServerAPI.MOBAPICallback callback)
    {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = FragmentBottomSheetLongMapBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initHeadView();
        initBodyView();
        initFooterView();
    }

    private void initHeadView()
    {
        coordinatesView = binding.coordinatesView;
        addressView = binding.addressView;
        markerTitleView = binding.markerTitleView;
        addMarkerButton = binding.addMarkerButton;

        String text =
                getString(R.string.bs_long_map_coordinates) + "\n\tX: " + latLng.latitude + "\n\tY: " + latLng.longitude;
        coordinatesView.setText(text);

        addressView.setText(R.string.bs_long_map_address);

        addMarkerButton.setOnClickListener(view ->
        {
            addMarkerButton.setVisibility(View.GONE);
            binding.cardView.setVisibility(View.VISIBLE);
        });
    }

    private void initBodyView()
    {
        imagesView = binding.imagesView;
        addImageButton = binding.addImageButton;

        addImageButton.setOnClickListener(view ->
        {
            launcher.launch("image/*");
        });
    }

    private void initFooterView()
    {
        binding.confirmAddingMarkerButton.setOnClickListener(view ->
        {
            if (callback == null) return;
            MainActivity.MOB_SERVER_API.post(callback, markerTitleView.getText()
                                                                      .toString(), latLng.latitude, latLng.longitude, imagePaths, MainActivity.token);
        });
    }

    private void onActivityResult(List<Uri> result)
    {
        if (result.size() == 0)
            return;

        List<Image> images = new ArrayList<>();
        for (Uri uri : result)
        {
            images.add(new Image(uri.getPath(), uri, Image.IMAGE_OFFLINE));
//                        imagePaths.add(UriUtils.getPath(getContext(), uri));
        }

        ImagesAdapter adapter = new ImagesAdapter((MainActivity) getActivity(), images);
        imagesView.setLayoutManager(new StaggeredGridLayoutManager(Math.min(images.size(), 3), StaggeredGridLayoutManager.VERTICAL));
        imagesView.setAdapter(adapter);
    }
}
