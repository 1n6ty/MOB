package com.example.mobv2.models;

public class Reaction
{
    public static String EMOJI_LIKE = "\uD83D\uDC4D";
    public static String EMOJI_DISLIKE = "\uD83D\uDC4E";
    public static String EMOJI_LOVE = "❤";

    private String emoji;
    private int count;
    private boolean add;

    public Reaction(String emoji,
                    int count)
    {
        this(emoji, count, false);
    }

    protected Reaction(String emoji,
                       int count,
                       boolean add)
    {
        this.emoji = emoji;
        this.count = count;
        this.add = add;
    }

    public static Reaction createAdd()
    {
        return new Reaction("+", -1, true);
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

    public boolean isAdd()
    {
        return add;
    }
}
