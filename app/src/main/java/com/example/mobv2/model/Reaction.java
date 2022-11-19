package com.example.mobv2.model;

import androidx.databinding.ObservableInt;

import com.example.mobv2.util.MyObservableArrayList;

import java.util.List;

public class Reaction
{
    private final String emoji;
    private final MyObservableArrayList<String> userIdsWhoLiked;

    private boolean checked;

    private final ObservableInt count;

    public Reaction(String emoji,
                    List<String> userIdsWhoLiked)
    {
        this(emoji, userIdsWhoLiked, false);
    }

    public Reaction(String emoji,
                    List<String> userIdsWhoLiked,
                    boolean checked)
    {
        this.emoji = emoji;
        this.userIdsWhoLiked = new MyObservableArrayList<>(userIdsWhoLiked);
        this.checked = checked;

        count = new ObservableInt(userIdsWhoLiked.size());
        this.userIdsWhoLiked.setOnListChangedCallback(new PostImpl.Operation(count, 1, -1));
    }

    public String getEmoji()
    {
        return emoji;
    }

    public List<String> getUserIdsWhoLiked()
    {
        return userIdsWhoLiked;
    }

    public ObservableInt getCount()
    {
        return count;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public boolean isChecked()
    {
        return checked;
    }
}
