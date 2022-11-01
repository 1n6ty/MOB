package com.example.mobv2.utils;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobv2.R;
import com.example.mobv2.adapters.ImagesAdapter;
import com.example.mobv2.adapters.ReactionsPostAdapter;
import com.example.mobv2.models.Image;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

//TODO rename please
public class ForPostsAndCommentsAdapters
{
    public static boolean onShowReactionsViewLongClick(Context context,
                                                       View view)
    {
        final int[] menuIds =
                {R.id.menu_reaction_like, R.id.menu_reaction_dislike, R.id.menu_reaction_love};

        var contextThemeWrapper =
                new ContextThemeWrapper(context, R.style.Theme_MOBv2_PopupOverlay);
        var popupMenu = new PopupMenu(contextThemeWrapper, view);
        popupMenu.inflate(R.menu.menu_reactions);

        popupMenu.show();

        var menu = popupMenu.getMenu();

        var reactionsView =
                (RecyclerView) view.getRootView()
                                   .findViewById(R.id.reactions_recycler_view);
        var reactionsAdapter = (ReactionsPostAdapter) reactionsView.getAdapter();
        for (int id : menuIds)
        {
            menu.findItem(id)
                .setOnMenuItemClickListener(item ->
                {
                    String emojiItem = item.getTitle()
                                           .toString();
               /* for (Reaction reaction : post.getReactions())
                {
                    String emoji = reaction.getEmoji();
                    if (emoji.equals(emojiItem))
                    {

                    }
                }*/
                    reactionsAdapter.addElement(emojiItem);
                    return true;
                });
        }

        return true;
    }

    public static void initPostContent(MainActivity mainActivity,
                                       @NonNull Pair<TextView, RecyclerView> content,
                                       PostImpl post)
    {
        content.first.setText(post.getText());

        switch (post.getType())
        {
            case PostImpl.POST_ONLY_TEXT:
                content.second.setVisibility(View.GONE);
                break;
            case PostImpl.POST_ONLY_IMAGES:
                content.first.setVisibility(View.GONE);
            case PostImpl.POST_FULL:
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
}
