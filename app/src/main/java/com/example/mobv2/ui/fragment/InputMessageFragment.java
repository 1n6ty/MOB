package com.example.mobv2.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentInputMessageBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.callback.OnSendButtonClickListener;
import com.example.mobv2.util.MessageViewTextWatcher;

public class InputMessageFragment extends BaseFragment<FragmentInputMessageBinding> implements HavingToolbar
{
    private Toolbar toolbar;
    private ImageButton sendButton;
    private EditText messageView;

    private OnSendButtonClickListener listener;

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

        initToolbar();

        initSendButton();
        initMessageView();

        return view;
    }

    @Override
    public void initToolbar()
    {
        toolbar = binding.toolbar;
        super.initToolbar(toolbar, "");
    }

    private void initSendButton()
    {
        sendButton = binding.sendButton;
        sendButton.setEnabled(false);
        sendButton.getDrawable()
                  .setTint(mainActivity.getAttributeColor(R.attr.colorHelpButtonIcon));
        sendButton.setOnClickListener(view ->
                listener.onClick(view, messageView.getText()
                                                  .toString()));
    }

    private void initMessageView()
    {
        messageView = binding.messageView;

        messageView.addTextChangedListener(new MessageViewTextWatcher(mainActivity, sendButton));
    }

    public void setOnSendButtonClickListener(OnSendButtonClickListener listener)
    {
        this.listener = ((view, text) ->
        {
            {
                listener.onClick(view, text);
                mainActivity.toPreviousFragment();
            }
        });
    }
}
