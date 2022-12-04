package com.example.mobv2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapter.CommentsAdapter;
import com.example.mobv2.callback.CommentCallback;
import com.example.mobv2.callback.abstraction.CommentOkCallback;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.model.CommentImpl;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.item.PostItem;
import com.example.mobv2.util.MessageViewTextWatcher;

public class CommentsFragment extends BaseFragment<FragmentCommentsBinding>
        implements HavingToolbar, Toolbar.OnMenuItemClickListener, CommentOkCallback
{
    private CommentsAdapter commentsAdapter;

    private PostItem postItem;

    public CommentsFragment()
    {
        super(R.layout.fragment_comments);
    }

    public void setPostItem(PostItem postItem)
    {
        this.postItem = postItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        binding.setPostItemHelper(postItem.postItemHelper);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();

        initPostView();

        initCommentsToolbar();
        initCommentsRecycler();
        initSendButton();
        initMessageView();
    }

    public void initToolbar()
    {
        super.initToolbar(binding.toolbar, "");
    }

    private void initPostView()
    {
        postItem.refreshItemBinding(binding.itemPost);
        postItem.getShowCommentsButton().setVisibility(View.GONE);
        postItem.hideDeleteMenuItem();
        postItem.hideEditMenuItem();
        postItem.hideForwardMenuItem();
    }

    private void initCommentsToolbar()
    {
        binding.commentsToolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        CommentsAdapter commentsAdapter = (CommentsAdapter) binding.commentsRecyclerView.getAdapter();
        if (commentsAdapter == null)
        {
            return false;
        }
        switch (item.getItemId())
        {
            case R.id.menu_posts_reverse:
                return commentsAdapter.reverse();
            case R.id.menu_sort_by_rates:
                return commentsAdapter.sortByRates();
            case R.id.menu_sort_by_date:
                return commentsAdapter.sortByDate();
            case R.id.menu_sort_by_comments:
                return commentsAdapter.sortByComments();
            default:
                return false;
        }
    }

    private void initCommentsRecycler()
    {
        var commentsRecyclerView = binding.commentsRecyclerView;
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter = new CommentsAdapter(mainActivity, binding.nestedScrollView,
                postItem.postItemHelper);
        commentsRecyclerView.setAdapter(commentsAdapter);
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
        var messageText = binding.messageView.getText();
        CommentCallback callback = new CommentCallback(mainActivity, messageText.toString());
        callback.setOkCallback(this::createCommentByIdAndTextAndAddToCommentIds);
        mainActivity.mobServerAPI.commentPost(callback, messageText.toString(),
                postItem.postItemHelper.getId(), MainActivity.token);
    }

    @Override
    public void createCommentByIdAndTextAndAddToCommentIds(String commentId,
                                                           String messageText)
    {
        var comment = CommentImpl.createNewComment(commentId,
                mainActivity.appDatabase.userDao().getCurrentOne(), messageText);

        mainActivity.appDatabase.commentDao().insert(comment);
        commentsAdapter.addElement(comment);
        postItem.postItemHelper.getCommentIds().add(commentId);

        onCommentCreated();
    }

    private void onCommentCreated()
    {
        var messageView = binding.messageView;
        messageView.getText().clear();
        messageView.clearFocus();
        var imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void initMessageView()
    {
        binding.messageView.addTextChangedListener(
                new MessageViewTextWatcher(mainActivity, binding.sendButton));
    }
}
