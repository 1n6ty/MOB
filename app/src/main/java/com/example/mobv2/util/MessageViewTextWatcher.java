package com.example.mobv2.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;

import com.example.mobv2.R;
import com.example.mobv2.ui.activity.MainActivity;

public class MessageViewTextWatcher implements TextWatcher
{
    private final MainActivity mainActivity;
    private final ImageButton sendButton;

    public MessageViewTextWatcher(MainActivity mainActivity,
                                  ImageButton sendButton)
    {
        this.mainActivity = mainActivity;
        this.sendButton = sendButton;
    }

    @Override
    public void beforeTextChanged(CharSequence s,
                                  int start,
                                  int count,
                                  int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s,
                              int start,
                              int before,
                              int count)
    {
        if (s.length() == 0)
        {
            sendButton.setEnabled(false);
            sendButton.getDrawable()
                      .setTint(mainActivity.getAttributeColor(R.attr.colorHelpButtonIcon));
        }
        else
        {
            sendButton.setEnabled(true);
            sendButton.getDrawable()
                      .setTint(mainActivity.getAttributeColor(android.R.attr.colorAccent));
        }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
}
