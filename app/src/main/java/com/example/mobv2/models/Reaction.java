package com.example.mobv2.models;

import java.util.List;

public class Reaction
{
    public static String EMOJI_LIKE = "\uD83D\uDC4D";
    public static String EMOJI_DISLIKE = "\uD83D\uDC4E";
    public static String EMOJI_LOVE = "❤";

    private final String emoji;
    private List<Integer> userIdsWhoLiked;
    private final boolean add;

    public Reaction(String emoji,
                    List<Integer> userIdsWhoLiked)
    {
        this(emoji, userIdsWhoLiked, false);
    }

    protected Reaction(String emoji,
                       List<Integer> userIdsWhoLiked,
                       boolean add)
    {
        this.emoji = emoji;
        this.userIdsWhoLiked = userIdsWhoLiked;
        this.add = add;
    }

    public static Reaction createAdd()
    {
        return new Reaction("+", null, true);
    }

    public String getEmoji()
    {
        return emoji;
    }

    public List<Integer> getUserIdsWhoLiked()
    {
        return userIdsWhoLiked;
    }

    public void setUserIdsWhoLiked(List<Integer> userIdsWhoLiked)
    {
        this.userIdsWhoLiked = userIdsWhoLiked;
    }

    public boolean isAdd()
    {
        return add;
    }

    public int getCount()
    {
        return userIdsWhoLiked == null ? -1 : userIdsWhoLiked.size();
    }
}
