package com.example.mobv2.ui.fragments.comments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.adapters.PostsAdapter;
import com.example.mobv2.adapters.ReactionsPostAdapter;
import com.example.mobv2.adapters.abstractions.ForPostsAndCommentsAdapters;
import com.example.mobv2.callbacks.CommentCallback;
import com.example.mobv2.callbacks.MOBAPICallbackImpl;
import com.example.mobv2.callbacks.abstractions.CommentOkCallback;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.ui.abstractions.HavingToolbar;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.utils.SimpleTextWatcher;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        var post = viewModel.getPostItem()
                            .getPost();
        super.initToolbar(toolbar, post.getTitle());
    }

    private void initPostView()
    {
        var itemPost = binding.itemPost;

        var post = viewModel.postItem.getPost();
        var user = post.getUser();
        String postId = post.getId();
        String userId = userDao.getCurrentId();

        itemPost.setCommentsCount(post.getCommentsCount());
        itemPost.setAppreciationsCount(post.getRatesCount());

        MainActivity.loadImageInView(user.getAvatarUrl(), itemPost.getRoot(), itemPost.avatarView);

        itemPost.fullNameView.setText(user.getFullName());

        itemPost.dateView.setText(new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(post.getDate()));

        itemPost.getRoot()
                .setOnClickListener(view -> onItemViewClick(view));

        if (post.getPositiveRates()
                .contains(userId))
        {
            itemPost.rateUpButton.setSelected(true);
        }
        else if (post.getNegativeRates()
                     .contains(userId))
        {
            itemPost.rateDownButton.setSelected(true);
        }

        itemPost.rateUpButton.setOnClickListener(view -> onRateUpButtonClick(view, itemPost.getRoot()));
        itemPost.rateDownButton.setOnClickListener(view -> onRateDownButtonClick(view, itemPost.getRoot()));

        itemPost.showReactionsView.setOnClickListener(view -> onShowReactionsViewClick(itemPost.reactionsRecyclerView));
        itemPost.showReactionsView.setOnLongClickListener(view -> ForPostsAndCommentsAdapters.onShowReactionsViewLongClick(mainActivity, view));

        Pair<TextView, RecyclerView> content =
                new Pair<>(itemPost.postTextView, itemPost.postImagesRecyclerView);
        ForPostsAndCommentsAdapters.initPostContent(mainActivity, content, post);

        var reactionsAdapter = viewModel.postItem.getReactionsAdapter();
        itemPost.reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        itemPost.reactionsRecyclerView.setAdapter(reactionsAdapter);

        itemPost.showCommentsView.setOnClickListener(view -> onCommentViewClick(view));
    }

    private void onItemViewClick(View view)
    {
        var contextThemeWrapper =
                new ContextThemeWrapper(mainActivity, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_post);

        initMenu(popupMenu);
        popupMenu.show();
    }

    private void initMenu(@NonNull PopupMenu popupMenu)
    {
        var menu = popupMenu.getMenu();
        var post = viewModel.postItem.getPost();
        var user = post.getUser();

        boolean isCreator =
                user.compareById(userDao.getCurrentOne());  // if the user is a post's creator
        menu.findItem(R.id.menu_edit_post)
            .setVisible(isCreator);
        menu.findItem(R.id.menu_delete_post)
            .setVisible(isCreator);

        menu.findItem(R.id.menu_copy_post)
            .setVisible(post.getType() == PostImpl.POST_ONLY_TEXT || post.getType() == PostImpl.POST_FULL);

        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item));
    }

    private boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_copy_post:
                return copyPost();
            default:
                return false;
        }
    }

    private void onRateUpButtonClick(View view,
                                     View rootView)
    {
        onRateButtonClick(view, rootView);
        var post = viewModel.postItem.getPost();
        removeRateFromFirstRatesAndAddRateToSecondRates(post.getNegativeRates(), post.getPositiveRates());

        mainActivity.mobServerAPI.postInc(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);
    }

    private void onRateDownButtonClick(View view,
                                       View rootView)
    {
        onRateButtonClick(view, rootView);
        var post = viewModel.postItem.getPost();
        removeRateFromFirstRatesAndAddRateToSecondRates(post.getPositiveRates(), post.getNegativeRates());

        mainActivity.mobServerAPI.postDec(new MOBAPICallbackImpl(), post.getId(), MainActivity.token);
    }

    private void onRateButtonClick(View view,
                                   View rootView)
    {
        var rateButton = (ImageButton) view;

        if (rateButton.isSelected())
        {
            rateButton.setSelected(false);
        }
        else
        {
            deselectRateButtons(rootView);
            rateButton.setSelected(true);
        }
    }

    private void deselectRateButtons(View rootView)
    {
        rootView.findViewById(R.id.rate_up_button)
                .setSelected(false);
        rootView.findViewById(R.id.rate_down_button)
                .setSelected(false);
    }

    private void removeRateFromFirstRatesAndAddRateToSecondRates(List<String> firstRates,
                                                                 List<String> secondRates)
    {
        String userId = userDao.getCurrentId();
        firstRates.remove(userId);
        if (!secondRates.remove(userId)) secondRates.add(userId);
    }

    private void onShowReactionsViewClick(View view)
    {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void onCommentViewClick(View view)
    {
        var post = viewModel.postItem.getPost();

        var reactionsAdapter = new ReactionsPostAdapter(mainActivity, post.getReactions(), post.getId());
        var postItem = new PostsAdapter.PostItem(post, reactionsAdapter);
        var viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
        viewModel.setPostItem(postItem);
        mainActivity.goToFragment(new CommentsFragment());
    }

    private boolean copyPost()
    {
        var post = viewModel.postItem.getPost();

        var clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText("simple text", post.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_LONG)
             .show();
        return true;
    }

    private void initCommentsRecycler()
    {
        commentsRecyclerView = binding.commentsRecyclerView;
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter = new CommentsAdapter(mainActivity, viewModel.getPostItem()
                                                                     .getPost());
        commentsRecyclerView.setAdapter(commentsAdapter);

//        var post = viewModel.getPostItem()
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
        var post = viewModel.getPostItem()
                            .getPost();
        var messageText = messageView.getText();
        CommentCallback callback = new CommentCallback(mainActivity);
        callback.setOkCallback(this::createCommentByIdAndAddToPosts);
        mainActivity.mobServerAPI.commentPost(callback, messageText.toString(), post.getId(), MainActivity.token);
    }

    @Override
    public void createCommentByIdAndAddToPosts(String commentId)
    {
        var post = viewModel.getPostItem()
                            .getPost();
        Editable messageText = messageView.getText();

        var comment =
                CommentImpl.createNewComment(commentId, post.getUser(), messageText.toString());
        commentsAdapter.addElement(comment);
        post.getCommentsIds()
            .add(commentId);

        messageText.clear();
    }
}
