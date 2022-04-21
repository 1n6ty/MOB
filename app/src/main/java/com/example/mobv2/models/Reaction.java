package com.example.mobv2.models;

public class Reaction
{
    public static String EMOJI_LIKE = "\uD83D\uDC4D";
    public static String EMOJI_DISLIKE = "\uD83D\uDC4E";
    public static String EMOJI_LOVE = "❤";

    private String emoji;
    private int count;

    public Reaction(String emoji,
                    int count)
    {
        this.emoji = emoji;
        this.count = count;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}
