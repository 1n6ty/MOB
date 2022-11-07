package com.example.mobv2.ui.fragments.comments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
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
import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.callbacks.CommentCallback;
import com.example.mobv2.callbacks.abstractions.CommentOkCallback;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.utils.SimpleTextWatcher;

import localdatabase.daos.UserDao;

public class CommentsFragment extends BaseFragment<FragmentCommentsBinding> implements HavingToolbar, CommentOkCallback
{
    private CommentsFragmentViewModel viewModel;
    private UserDao userDao;

    private Toolbar toolbar;
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

        initUserDao();

        initViewModel();
        binding.setBindingContext(viewModel);

        return view;
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

        initCommentsRecycler();
        initMessageView();
        initSendButton();
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
        postItem.getShowCommentsView()
                .setVisibility(View.GONE);
        postItem.hideDeleteMenuItem();
        postItem.hideEditMenuItem();
        postItem.hideForwardMenuItem();
    }

    private void initCommentsRecycler()
    {
        var postItem = viewModel.postItem;
        commentsRecyclerView = binding.commentsRecyclerView;
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter = new CommentsAdapter(mainActivity, postItem.postItemHelper);
        commentsRecyclerView.setAdapter(commentsAdapter);

//        var post = viewModel.postItem
//                            .getPost();
//
//        for (String commentId : post.getCommentsIds())
//        {
//            MainActivity.MOB_SERVER_API.getComment(new GetCommentCallback(mainActivity, commentsRecycler),
//                    commentId, MainActivity.token);
//        }
    }

    private void initMessageView()
    {
        messageView = binding.messageView;

        messageView.addTextChangedListener(new SimpleTextWatcher()
        {
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
        });
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
        callback.setOkCallback(this::createCommentByIdAndAddToPosts);
        mainActivity.mobServerAPI.commentPost(callback, messageText.toString(), postView.postItemHelper.getId(), MainActivity.token);
    }

    @Override
    public void createCommentByIdAndAddToPosts(String commentId)
    {
        var postView = viewModel.postItem;
        Editable messageText = messageView.getText();

        var comment =
                CommentImpl.createNewComment(commentId, postView.postItemHelper.getUser(), messageText.toString());
        var commentItem = commentsAdapter.addElementAndGet(comment);
//        commentItem.commentItemHelper.setHavingCommentsIds(postView.postItemHelper);
        postView.postItemHelper.getCommentIds()
                               .add(commentId);

        messageText.clear();
    }
}
