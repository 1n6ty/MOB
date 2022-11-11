package com.example.mobv2.ui.fragments.markercreators;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.ImagesAdapter;
import com.example.mobv2.databinding.FragmentMarkerCreatorBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.utils.UriUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import serverapi.MOBServerAPI;

public class MarkerCreatorFragment extends BaseFragment<FragmentMarkerCreatorBinding>
        implements HavingToolbar, ActivityResultCallback<List<Uri>>
{
    private final ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), this);

    private final List<File> files;

    private MarkerCreatorViewModel viewModel;

    private Toolbar toolbar;
    private EditText markerTitleView;
    private EditText markerTextView;
    private RecyclerView imagesRecyclerView;
    private ImageView addImageButton;

    public MarkerCreatorFragment()
    {
        super(R.layout.fragment_marker_creator);

        files = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();

        initToolbar();

        initTextInfo();
        initImagesInfo();
        initConfirmAddingMarkerButton();
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(MarkerCreatorViewModel.class);
    }

    @Override
    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "Create marker");
    }

    private void initTextInfo()
    {
        markerTitleView = binding.markerTitleView;
        markerTextView = binding.markerTextView;
    }

    private void initImagesInfo()
    {
        addImageButton = binding.addImageButton;
        imagesRecyclerView = binding.imagesRecyclerView;

        addImageButton.setOnClickListener(view ->
        {
            launcher.launch("image/*");
        });
    }

    private void initConfirmAddingMarkerButton()
    {
        binding.confirmAddingMarkerButton.setOnClickListener(this::onConfirmAddingMarkerButtonClick);
    }

    public void onActivityResult(List<Uri> result)
    {
        if (result.size() == 0)
        {
            return;
        }

        List<Image> images = new ArrayList<>();
        for (Uri uri : result)
        {
            File file = new UriUtils(getContext()).getFileFromUri(uri);
            files.add(file);
            images.add(new Image(uri.getPath(), uri, Image.IMAGE_OFFLINE));
        }

        ImagesAdapter adapter = new ImagesAdapter((MainActivity) getActivity(), images);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imagesRecyclerView.setAdapter(adapter);
    }

    private void onConfirmAddingMarkerButtonClick(View view)
    {
        LatLng latLng = viewModel.getAddress()
                                 .getLatLng();
        MOBServerAPI.MOBAPICallback callback = viewModel.getCallback();

        String text = markerTextView.getText()
                                    .toString();
        String title = markerTitleView.getText()
                                      .toString();
        mainActivity.mobServerAPI.post(callback, text, title, latLng.latitude, latLng.longitude, files.toArray(new File[0]), MainActivity.token);

        mainActivity.toPreviousFragment();
    }
}
