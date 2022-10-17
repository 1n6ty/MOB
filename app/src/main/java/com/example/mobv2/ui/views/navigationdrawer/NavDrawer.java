package com.example.mobv2.ui.views.navigationdrawer;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.databinding.NavHeaderMainBinding;
import com.example.mobv2.models.MenuItemMetadatum;
import com.example.mobv2.models.UserImpl;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.EditProfileFragment;
import com.example.mobv2.ui.fragments.LanguageFragment;
import com.example.mobv2.ui.fragments.MapFeaturesFragment;
import com.example.mobv2.ui.fragments.NotificationAndSoundFragment;
import com.example.mobv2.ui.fragments.main.MainFragmentViewModel;
import com.google.android.material.navigation.NavigationView;

import java.net.MalformedURLException;
import java.net.URL;

public class NavDrawer implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int PROFILE_GROUP = 0, SETTINGS_GROUP = 1;

    private final UserImpl user;

    private final MainActivity mainActivity;

    private final DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;

    public NavDrawer(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        user = mainActivity.appDatabase.userDao()
                                       .getOne();

        drawerLayout = mainActivity.findViewById(R.id.drawer_layout);

        initNavigationView();
        updateHeaderView();
        initNavigationMenu();
    }

    private void initNavigationView()
    {
        navigationView = mainActivity.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);

        var binding = NavHeaderMainBinding.bind(navigationView.getHeaderView(0));
        var mainFragmentViewModel =
                new ViewModelProvider(mainActivity).get(MainFragmentViewModel.class);
        binding.setBindingContext(mainFragmentViewModel);
    }

    private void updateHeaderView()
    {
        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + user.getAvatarUrl());
        }
        catch (MalformedURLException e)
        {
            return;
        }

        ImageView avatarView = headerView.findViewById(R.id.avatar_view);
        Glide.with(mainActivity)
             .load(url)
             .into(avatarView);
    }

    private void initNavigationMenu()
    {
        Menu menu = navigationView.getMenu();
        menu.clear();

        int order = 0;

        addNavigationMenuItem(menu,
                PROFILE_GROUP,
                order++,
                R.string.menu_edit_profile,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT,
                        () ->
                        {
                            mainActivity.goToFragment(new EditProfileFragment());
                        }),
                R.drawable.ic_menu_profile
        );

        // the next submenu
        Menu submenuSettings =
                menu.addSubMenu(SETTINGS_GROUP, Menu.NONE, order, R.string.nav_header_item_settings);

        addNavigationMenuItem(submenuSettings,
                SETTINGS_GROUP,
                order++,
                R.string.menu_notification_and_sound,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT,
                        () ->
                        {
                            mainActivity.goToFragment(new NotificationAndSoundFragment());
                        }),
                R.drawable.ic_menu_notification
        );

        addNavigationMenuItem(submenuSettings,
                SETTINGS_GROUP,
                order++,
                R.string.menu_map_features,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT,
                        () ->
                        {
                            mainActivity.goToFragment(new MapFeaturesFragment());
                        }),
                R.drawable.ic_menu_map
        );

        addNavigationMenuItem(submenuSettings,
                SETTINGS_GROUP,
                order,
                R.string.menu_language,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT,
                        () ->
                        {
                            mainActivity.goToFragment(new LanguageFragment());
                        }),
                R.drawable.ic_menu_international
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        NavDrawerViewModel viewModel =
                new ViewModelProvider(mainActivity).get(NavDrawerViewModel.class);
        MenuItemMetadatum metadatum = viewModel.getMenuItemHash(item);

        switch (metadatum.itemType)
        {
            case MenuItemMetadatum.ITEM_FRAGMENT:
            case MenuItemMetadatum.ITEM_SITE:
                metadatum.listener.onClick();
                break;
        }

        close();

        return true;
    }


    private void addNavigationMenuItem(Menu menu,
                                       int groupId,
                                       int order,
                                       @StringRes int title,
                                       MenuItemMetadatum menuItemMetadatum,
                                       @DrawableRes int icon)
    {
        addNavigationMenuItem(menu, groupId, order, mainActivity.getString(title), menuItemMetadatum, icon);
    }

    private void addNavigationMenuItem(Menu menu,
                                       int groupId,
                                       int order,
                                       CharSequence title,
                                       MenuItemMetadatum menuItemMetadatum,
                                       @DrawableRes int icon)
    {
        MenuItem menuItem = menu.add(groupId, order, order, title)
                                .setIcon(icon);

        NavDrawerViewModel viewModel =
                new ViewModelProvider(mainActivity).get(NavDrawerViewModel.class);

        viewModel.putMenuItemHash(menuItem, menuItemMetadatum);
    }


    public boolean isOpen()
    {
        return drawerLayout.isDrawerOpen(navigationView);
    }

    public void open()
    {
        drawerLayout.openDrawer(navigationView);
    }

    public boolean isClosed()
    {
        return !drawerLayout.isDrawerOpen(navigationView);
    }

    public void close()
    {
        drawerLayout.closeDrawer(navigationView);
    }
}