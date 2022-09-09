package com.example.mobv2.ui.fragments.comments;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapters.CommentsAdapter;
import com.example.mobv2.adapters.ImagesAdapter;
import com.example.mobv2.callbacks.CommentCallback;
import com.example.mobv2.callbacks.GetCommentCallback;
import com.example.mobv2.databinding.FragmentCommentsBinding;
import com.example.mobv2.models.Image;
import com.example.mobv2.models.Post;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.BaseFragment;
import com.example.mobv2.utils.SimpleTextWatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends BaseFragment<FragmentCommentsBinding>
{
    private CommentsFragmentViewModel viewModel;

    private Toolbar toolbar;
    private RecyclerView commentsRecycler;
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

        initViewModel();
        binding.setBindingContext(viewModel);

        return view;
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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(mainActivity).get(CommentsFragmentViewModel.class);
    }

    protected void initToolbar()
    {
        toolbar = binding.toolbar;
        var post = viewModel.getPostItem()
                            .getPost();
        super.initToolbar(toolbar, post.getTitle());
    }

    private void initPostView()
    {
        var post = viewModel.getPostItem()
                            .getPost();
        var user = post.getUser();

        var itemPost = binding.itemPost;

        MainActivity.loadImageInView(user.getAvatarUrl(), itemPost.getRoot(), itemPost.avatarView);

        itemPost.fullnameView.setText(user.getFullname());

        itemPost.dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(post.getDate()));

//        itemPost.appreciationUpButton.setOnClickListener(view -> onAppreciationUpClick(view, position));
        itemPost.appreciationsCountView.setText(String.valueOf(post.getAppreciationsCount()));
//        itemPost.appreciationDownButton.setOnClickListener(this::onAppreciationDownClick);
        itemPost.appreciationUpButton.setSelected(false);
//        itemPost.appreciationUpButton.getDrawable().setTint(0);
        itemPost.appreciationDownButton.setSelected(false);
//        itemPost.appreciationDownButton.getDrawable().setTint(0);
        if (post.getAppreciated() == 1)
        {
            itemPost.appreciationUpButton.setSelected(true);
            itemPost.appreciationUpButton.getButtonDrawable()
                                       .setTint(mainActivity.getAttribute(androidx.appcompat.R.attr.colorAccent));
        }
        else if (post.getAppreciated() == 0)
        {
            itemPost.appreciationDownButton.setSelected(true);
            itemPost.appreciationDownButton.getButtonDrawable()
                                         .setTint(mainActivity.getAttribute(androidx.appcompat.R.attr.colorAccent));
        }

        itemPost.showReactionsView.setOnClickListener(view -> viewModel.getPostItem()
                                                                       .onShowReactionsViewClick(itemPost.reactionsView));

        Pair<TextView, RecyclerView> content =
                new Pair<>(itemPost.postTextView, itemPost.postImagesView);
        initPostContent(content);

        itemPost.reactionsView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        itemPost.reactionsView.setAdapter(viewModel.getPostItem()
                                                   .getReactionsAdapter());

        itemPost.commentView.setVisibility(View.GONE);
    }

    private void initPostContent(@NonNull Pair<TextView, RecyclerView> content)
    {
        var post = viewModel.getPostItem()
                            .getPost();

        switch (post.getType())
        {
            case Post.POST_ONLY_TEXT:
                content.first.setText(post.getText());
                content.second.setVisibility(View.GONE);
                break;
            case Post.POST_ONLY_IMAGES:
                content.first.setVisibility(View.GONE);
            case Post.POST_FULL:
                content.first.setText(post.getText());
                List<Image> images = new ArrayList<>();
                for (String url : post.getImages())
                {
                    images.add(new Image("", url, Image.IMAGE_ONLINE));
                }
                ImagesAdapter adapter = new ImagesAdapter(mainActivity, images);
                content.second.setLayoutManager(new StaggeredGridLayoutManager(Math.min(images.size(), 3), StaggeredGridLayoutManager.VERTICAL));
                content.second.setAdapter(adapter);
                break;
        }
    }

    private void initCommentsRecycler()
    {
        commentsRecycler = binding.commentsRecycler;
        commentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter = new CommentsAdapter(mainActivity, viewModel.getPostItem()
                                                                     .getPost(), new ArrayList<>());
        commentsRecycler.setAdapter(commentsAdapter);

        var post = viewModel.getPostItem()
                            .getPost();
        for (int i = 0; i < viewModel.getCommentsCount(); i++)
        {
            MainActivity.MOB_SERVER_API.getComment(new GetCommentCallback(mainActivity, commentsRecycler, true), post.getId(), i, true, MainActivity.token);
        }
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
                              .setTint(getResources().getColor(R.color.gray_200));
                }

                else
                {
                    sendButton.setEnabled(true);
                    sendButton.getDrawable()
                              .setTint(getResources().getColor(mainActivity.getAttribute(android.R.attr.colorAccent)));
                }
            }
        });
    }

    private void initSendButton()
    {
        sendButton = binding.sendButton;
        sendButton.setEnabled(false);
        sendButton.getDrawable()
                  .setTint(getResources().getColor(R.color.gray_200));
        sendButton.setOnClickListener(v ->
        {
            var post = viewModel.getPostItem()
                                .getPost();
            var messageText = messageView.getText();
            MainActivity.MOB_SERVER_API.comment(new CommentCallback(mainActivity, commentsRecycler, post.getId()), messageText.toString(), post.getId(), MainActivity.token);
            messageText.clear();
        });
    }


}
