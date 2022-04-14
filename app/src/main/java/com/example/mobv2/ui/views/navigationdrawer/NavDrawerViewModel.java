package com.example.mobv2.ui.views.navigationdrawer;

import android.view.MenuItem;

import androidx.lifecycle.ViewModel;

import com.example.mobv2.models.MenuItemMetadatum;

import java.util.HashMap;

public class NavDrawerViewModel extends ViewModel
{
    private final HashMap<CharSequence, MenuItemMetadatum> menuItemHashMap = new HashMap<>();


    public MenuItemMetadatum getMenuItemHash(MenuItem item)
    {
        return menuItemHashMap.get(item.getTitle());
    }

    public void putMenuItemHash(MenuItem item, MenuItemMetadatum itemMetadatum)
    {
        menuItemHashMap.put(item.getTitle(), itemMetadatum);
    }
}
