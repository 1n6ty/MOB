package com.example.mobv2.ui.fragment.comment;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapter.CommentsAdapter;
import com.example.mobv2.callback.CommentCallback;
import com.example.mobv2.callback.abstraction.CommentOkCallback;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.model.CommentImpl;
import com.example.mobv2.ui.abstraction.HavingToolbar;
import com.example.mobv2.ui.activity.MainActivity;
import com.example.mobv2.ui.fragment.BaseFragment;
import com.example.mobv2.util.MessageViewTextWatcher;

import localDatabase.dao.CommentDao;
import localDatabase.dao.UserDao;

public class CommentsFragment extends BaseFragment<FragmentCommentsBinding>
        implements HavingToolbar, Toolbar.OnMenuItemClickListener, CommentOkCallback
{
    private CommentsFragmentViewModel viewModel;
    private CommentDao commentDao;
    private UserDao userDao;

    private Toolbar toolbar;
    private Toolbar commentsToolbar;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private EditText messageView;
    private ImageButton sendButton;

    public CommentsFragment()
    {
        super(R.layout.fragment_comments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        var view = super.onCreateView(inflater, container, savedInstanceState);

        initCommentDao();
        initUserDao();

        initViewModel();
        binding.setBindingContext(viewModel);

        return view;
    }

    private void initCommentDao()
    {
        commentDao = mainActivity.appDatabase.commentDao();
    }

    private void initUserDao()
    {
        userDao = mainActivity.appDatabase.userDao();
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
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
        toolbar = binding.toolbar;
        var postItem = viewModel.postItem;
        super.initToolbar(toolbar, postItem.postItemHelper.getTitle());
    }

    private void initPostView()
    {
        var postItem = viewModel.postItem;
        postItem.refreshItemBinding(binding.itemPost);
        postItem.getShowCommentsButton()
                .setVisibility(View.GONE);
        postItem.hideDeleteMenuItem();
        postItem.hideEditMenuItem();
        postItem.hideForwardMenuItem();
    }

    private void initCommentsToolbar()
    {
        commentsToolbar = binding.commentsToolbar;

        commentsToolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        CommentsAdapter commentsAdapter = (CommentsAdapter) commentsRecyclerView.getAdapter();
        if (commentsAdapter == null) return false;
        switch (item.getItemId())
        {
            case R.id.menu_posts_reverse:
                return commentsAdapter.reverse();
            case R.id.menu_sort_by_rates:
                return commentsAdapter.sortByAppreciations();
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
        var postItem = viewModel.postItem;
        commentsRecyclerView = binding.commentsRecyclerView;
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter =
                new CommentsAdapter(mainActivity, binding.nestedScrollView, postItem.postItemHelper);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void initSendButton()
    {
        sendButton = binding.sendButton;
        sendButton.setEnabled(false);
        sendButton.getDrawable()
                  .setTint(mainActivity.getAttributeColor(R.attr.colorHelpButtonIcon));
        sendButton.setOnClickListener(this::onSendButtonClick);
    }

    private void onSendButtonClick(View view)
    {
        var postView = viewModel.postItem;
        var messageText = messageView.getText();
        CommentCallback callback = new CommentCallback(mainActivity);
        callback.setOkCallback(this::createCommentByIdAndAddToCommentIds);
        mainActivity.mobServerAPI.commentPost(callback, messageText.toString(), postView.postItemHelper.getId(), MainActivity.token);
    }

    @Override
    public void createCommentByIdAndAddToCommentIds(String commentId)
    {
        var postView = viewModel.postItem;
        Editable messageText = messageView.getText();

        var comment =
                CommentImpl.createNewComment(commentId, userDao.getCurrentOne(), messageText.toString());

        commentDao.insert(comment);
        commentsAdapter.addElement(comment);
        postView.postItemHelper.getCommentIds()
                               .add(commentId);

        messageText.clear();
    }

    private void initMessageView()
    {
        messageView = binding.messageView;

        messageView.addTextChangedListener(new MessageViewTextWatcher(mainActivity, sendButton));
    }
}