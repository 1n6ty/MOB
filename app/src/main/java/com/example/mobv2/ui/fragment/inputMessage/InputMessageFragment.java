package com.example.mobv2.ui.fragment.inputMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.callback.CommentCallback;
import com.example.mobv2.databinding.FragmentInputMessageBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;
import com.example.mobv2.util.MessageViewTextWatcher;

public class InputMessageFragment extends BaseFragment<FragmentInputMessageBinding> implements HavingToolbar
{
    private InputMessageFragmentViewModel viewModel;

    public InputMessageFragment()
    {
        super(R.layout.fragment_input_message);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        initViewModel();

        return view;
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(InputMessageFragmentViewModel.class);
        viewModel.setActive(true);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initSendButton();
        initMessageView();
    }

    @Override
    public void initToolbar()
    {
        super.initToolbar(binding.toolbar, "");
    }

    private void initSendButton()
    {
        var sendButton = binding.sendButton;
        sendButton.setEnabled(false);
        sendButton.getDrawable()
                  .setTint(mainActivity.getAttributeColor(R.attr.colorHelpButtonIcon));
        sendButton.setOnClickListener(this::onSendButtonClick);
    }

    private void onSendButtonClick(View view)
    {
        var messageView = binding.messageView;
        var messageText = messageView.getText()
                                     .toString();
        CommentCallback callback = new CommentCallback(mainActivity, messageView.getText()
                                                                                .toString());
        callback.setOkCallback(viewModel.createCommentByIdAndAddToCommentIds);
        mainActivity.mobServerAPI.commentComment(callback, messageText, viewModel.parentId, MainActivity.token);

        mainActivity.toPreviousFragment();
    }

    private void initMessageView()
    {
        binding.messageView.addTextChangedListener(new MessageViewTextWatcher(mainActivity, binding.sendButton));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        viewModel.setActive(false);
    }
}
