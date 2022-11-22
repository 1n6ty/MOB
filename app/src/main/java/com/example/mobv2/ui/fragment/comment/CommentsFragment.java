package com.example.mobv2.ui.fragment.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    private CommentsAdapter commentsAdapter;

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
        var postItem = viewModel.postItem;
        super.initToolbar(binding.toolbar, postItem.postItemHelper.getTitle());
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
        binding.commentsToolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        CommentsAdapter commentsAdapter =
                (CommentsAdapter) binding.commentsRecyclerView.getAdapter();
        if (commentsAdapter == null) return false;
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
        var postItem = viewModel.postItem;
        var commentsRecyclerView = binding.commentsRecyclerView;
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter =
                new CommentsAdapter(mainActivity, binding.nestedScrollView, postItem.postItemHelper);
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
        var postView = viewModel.postItem;
        var messageText = binding.messageView.getText();
        CommentCallback callback = new CommentCallback(mainActivity, messageText.toString());
        callback.setOkCallback(this::createCommentByIdAndTextAndAddToCommentIds);
        mainActivity.mobServerAPI.commentPost(callback, messageText.toString(), postView.postItemHelper.getId(), MainActivity.token);
    }

    @Override
    public void createCommentByIdAndTextAndAddToCommentIds(String commentId,
                                                           String messageText)
    {
        var postView = viewModel.postItem;

        var comment =
                CommentImpl.createNewComment(commentId, userDao.getCurrentOne(), messageText);

        commentDao.insert(comment);
        commentsAdapter.addElement(comment);
        postView.postItemHelper.getCommentIds()
                               .add(commentId);

        binding.messageView.getText()
                           .clear();
    }

    private void initMessageView()
    {
        binding.messageView.addTextChangedListener(new MessageViewTextWatcher(mainActivity, binding.sendButton));
    }
}
