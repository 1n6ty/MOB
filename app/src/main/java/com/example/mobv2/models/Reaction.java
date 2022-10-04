package com.example.mobv2.models;

import java.util.List;

public class Reaction
{
    private final String emoji;
    private final List<String> userIdsWhoLiked;

    private boolean checked;

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
        this.userIdsWhoLiked = userIdsWhoLiked;
        this.checked = checked;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public List<String> getUserIdsWhoLiked()
    {
        return userIdsWhoLiked;
    }

    public int getCount()
    {
        return userIdsWhoLiked == null ? -1 : userIdsWhoLiked.size();
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
