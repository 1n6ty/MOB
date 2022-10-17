package com.example.mobv2.ui.fragments.markercreators;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.databinding.FragmentMapSkillsBottomSheetBinding;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MapSkillsBottomSheetFragment extends BottomSheetDialogFragment
        implements HavingToolbar
{
    private OnDestroyViewListener onDestroyViewListener;

    private MarkerCreatorViewModel viewModel;

    private FragmentMapSkillsBottomSheetBinding binding;
    private Toolbar toolbar;
    private Button createAddressMarkerButton;
    private Button createSubAddressMarkerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMapSkillsBottomSheetBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();

        initToolbar();

        initBodyView();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if (onDestroyViewListener != null) onDestroyViewListener.onDestroyView(getView());
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(requireActivity()).get(MarkerCreatorViewModel.class);
    }

    @Override
    public void initToolbar()
    {
        toolbar = binding.toolbar;

        String secondaryAddressTitle = viewModel.getAddress()
                                                .getSecondary();
        toolbar.setTitle(secondaryAddressTitle);

        LatLng latLng = viewModel.getLatLng();
        String coordinatesSubtitle = latLng.latitude + " : " + latLng.longitude;
        toolbar.setSubtitle(coordinatesSubtitle);
        toolbar.setOnClickListener(this::onToolbarClick);
    }

    private void onToolbarClick(View view)
    {
        var clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence clipText = clipboard.getPrimaryClip()
                                         .getItemAt(0)
                                         .getText();
        CharSequence subtitle = toolbar.getSubtitle()
                                       .toString()
                                       .replace(" : ", " ");
        if (!clipText.equals(subtitle))
        {
            var clip = ClipData.newPlainText("simple text", subtitle);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(), "Copied", Toast.LENGTH_LONG)
                 .show();
        }
    }

    private void initBodyView()
    {
        createAddressMarkerButton = binding.createAddressMarkerButton;
        createSubAddressMarkerButton = binding.createSubAddressMarkerButton;

        createAddressMarkerButton.setOnClickListener(view -> Toast.makeText(getContext(), "Not exist", Toast.LENGTH_LONG)
                                                                  .show());
        createSubAddressMarkerButton.setOnClickListener(view ->
        {
            ((MainActivity) getActivity()).goToFragment(new MarkerCreatorFragment());
            dismiss();
        });
    }

    public void setOnDestroyViewListener(OnDestroyViewListener onDestroyViewListener)
    {
        this.onDestroyViewListener = onDestroyViewListener;
    }

    public interface OnDestroyViewListener
    {
        void onDestroyView(View view);
    }
}