package com.example.mobv2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.mobv2.R;
import com.example.mobv2.callback.CommentCallback;
import com.example.mobv2.callback.abstraction.CommentOkCallback;
import com.example.mobv2.databinding.FragmentInputMessageBinding;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.util.MessageViewTextWatcher;

public class InputMessageFragment extends BaseFragment<FragmentInputMessageBinding>
        implements HavingToolbar
{
    public static boolean active = false;

    private String parentId;
    private CommentOkCallback commentOkCallback;

    public InputMessageFragment()
    {
        super(R.layout.fragment_input_message);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        var transitionNo = TransitionInflater.from(mainActivity)
                                             .inflateTransition(android.R.transition.no_transition);
        setExitTransition(transitionNo);
        setEnterTransition(transitionNo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        active = true;

        return view;
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

        CommentCallback commentCallback = new CommentCallback(mainActivity,
                messageView.getText().toString());
        commentCallback.setOkCallback((commentId, messageText) ->
        {
            commentOkCallback.createCommentByIdAndTextAndAddToCommentIds(commentId, messageText);
            Navigation.findNavController(requireActivity(), R.id.nav_content_frame).popBackStack();
        });

        mainActivity.mobServerAPI.commentComment(commentCallback, messageView.getText().toString(),
                parentId, MainActivity.token);
    }

    private void initMessageView()
    {
        var messageView = binding.messageView;
        messageView.addTextChangedListener(
                new MessageViewTextWatcher(mainActivity, binding.sendButton));
        messageView.requestFocus(View.FOCUS_DOWN);
        var imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        active = false;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public void setCommentOkCallback(CommentOkCallback commentOkCallback)
    {
        this.commentOkCallback = commentOkCallback;
    }
}
