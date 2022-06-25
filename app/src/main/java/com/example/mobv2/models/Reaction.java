package com.example.mobv2.models;

public class Reaction
{
    public static int EMOJI = 0, ADD = 1;

    public static String EMOJI_LIKE = "\uD83D\uDC4D";
    public static String EMOJI_DISLIKE = "\uD83D\uDC4E";
    public static String EMOJI_LOVE = "❤";

    public static String PLUS = "+";

    private String emoji;
    private int count;
    private int type;

    public Reaction(String emoji,
                    int count)
    {
        this.emoji = emoji;
        this.count = count;
        this.type = emoji.equals(PLUS) ? ADD : EMOJI;
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

    public int getType()
    {
        return type;
    }
}
