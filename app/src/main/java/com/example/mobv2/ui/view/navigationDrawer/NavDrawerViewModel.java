package com.example.mobv2.ui.view.navigationDrawer;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.mobv2.model.MenuItemMetadatum;

import java.util.HashMap;

public class NavDrawerViewModel extends ViewModel
{
    private final HashMap<CharSequence, MenuItemMetadatum> menuItemHashMap = new HashMap<>();

    public MenuItemMetadatum getMenuItemHash(@NonNull MenuItem item)
    {
        return menuItemHashMap.get(item.getTitle());
    }

    public void putMenuItemHash(@NonNull MenuItem item,
                                MenuItemMetadatum itemMetadatum)
    {
        menuItemHashMap.put(item.getTitle(), itemMetadatum);
    }
}
