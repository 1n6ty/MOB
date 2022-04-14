package com.example.mobv2.models;

public class MenuItemMetadatum
{
    public static final int ITEM_FRAGMENT = 0, ITEM_SITE = 1;

    public int itemType;
    public OnClickListener listener;

    public MenuItemMetadatum(int itemType, OnClickListener listener)
    {
        this.itemType = itemType;
        this.listener = listener;
    }

    public interface OnClickListener
    {
        void onClick();
    }
}
