package com.example.mobv2.ui.fragment.markerCreator;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapter.ImagesAdapter;
import com.example.mobv2.databinding.FragmentMarkerCreatorBinding;
import com.example.mobv2.model.Image;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;
import com.example.mobv2.util.Navigator;
import com.example.mobv2.util.UriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MarkerCreatorFragment extends BaseFragment<FragmentMarkerCreatorBinding>
        implements HavingToolbar, ActivityResultCallback<List<Uri>>
{
    private final ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), this);

    private final List<File> files;

    private MarkerCreatorViewModel viewModel;

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
        super.initToolbar(binding.toolbar, "Create marker");
    }

    private void initImagesInfo()
    {
        binding.addImageButton.setOnClickListener(view ->
        {
            launcher.launch("image/*");
        });
    }

    private void initConfirmAddingMarkerButton()
    {
        binding.confirmAddingMarkerButton.setOnClickListener(
                this::onConfirmAddingMarkerButtonClick);
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

        var adapter = new ImagesAdapter((MainActivity) getActivity(), images);
        var imagesRecyclerView = binding.imagesRecyclerView;
        imagesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setAdapter(adapter);
    }

    private void onConfirmAddingMarkerButtonClick(View view)
    {
        var latLng = viewModel.getAddress().getLatLng();

        String text = binding.markerTextView.getText().toString();
        String title = binding.markerTitleView.getText().toString();
        mainActivity.mobServerAPI.post(viewModel.createPostCallback, text, title, latLng.latitude,
                latLng.longitude, files.toArray(new File[0]), MainActivity.token);

        Navigator.toPreviousFragment();
    }
}
