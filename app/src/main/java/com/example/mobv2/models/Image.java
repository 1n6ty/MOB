package com.example.mobv2.models;

public class Image
{
    public static final int IMAGE_ONLINE = 0;
    public static final int IMAGE_OFFLINE = 1;

    private final String name;
    private final Object path;

    private final int type;

    public Image(String name,
                 Object path,
                 int type)
    {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Object getPath()
    {
        return path;
    }

    public int getType()
    {
        return type;
    }
}
