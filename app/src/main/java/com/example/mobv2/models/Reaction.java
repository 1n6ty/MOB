package com.example.mobv2.models;

import java.util.List;

public class Reaction
{
    public static String EMOJI_LIKE = "\uD83D\uDC4D";
    public static String EMOJI_DISLIKE = "\uD83D\uDC4E";
    public static String EMOJI_LOVE = "❤";

    private final String emoji;
    private final List<Integer> userIdsWhoLiked;

    private boolean checked;

    public Reaction(String emoji,
                    List<Integer> userIdsWhoLiked)
    {
        this(emoji, userIdsWhoLiked, false);
    }

    public Reaction(String emoji,
                    List<Integer> userIdsWhoLiked,
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

    public List<Integer> getUserIdsWhoLiked()
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
