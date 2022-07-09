package com.example.mobv2.ui.views.navigationdrawer;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobv2.R;
import com.example.mobv2.models.MenuItemMetadatum;
import com.example.mobv2.ui.activities.MainActivity;
import com.example.mobv2.ui.fragments.EditProfileFragment;
import com.example.mobv2.ui.fragments.LanguageFragment;
import com.example.mobv2.ui.fragments.MapFeaturesFragment;
import com.example.mobv2.ui.fragments.NotificationAndSoundFragment;
import com.google.android.material.navigation.NavigationView;

import java.net.MalformedURLException;
import java.net.URL;

public class NavDrawer implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int PROFILE_GROUP = 0, SETTINGS_GROUP = 1;

    private final MainActivity mainActivity;

    private final DrawerLayout drawerLayout;
    private NavigationView navigationView;


    public NavDrawer(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        drawerLayout = mainActivity.findViewById(R.id.drawer_layout);

        initNavigationView();
        initHeaderView();
        initNavigationMenu();
    }

    private void initNavigationView()
    {
        navigationView = mainActivity.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void initHeaderView()
    {
        View headerView = navigationView.getHeaderView(0);

        URL url;
        try
        {
            url = new URL("http://192.168.0.104:8000" + mainActivity.getPrivatePreferences()
                                                                    .getString(MainActivity.USER_AVATAR_URL_KEY, ""));
        }
        catch (MalformedURLException e)
        {
            return;
        }

        ImageView avatarView = headerView.findViewById(R.id.avatar_view);
        Glide.with(navigationView)
             .load(url)
             .into(avatarView);

        TextView fullNameView = headerView.findViewById(R.id.fullname_view);
        fullNameView.setText(mainActivity.getPrivatePreferences()
                                         .getString(MainActivity.USER_FULLNAME_KEY, ""));

        TextView addressesView = headerView.findViewById(R.id.address_view);
        addressesView.setText(mainActivity.getPrivatePreferences()
                                          .getString(MainActivity.ADDRESS_FULL_KEY, "No selected address"));
    }

    public void initNavigationMenu()
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
                            mainActivity.transactionToFragment(new EditProfileFragment());
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
                            mainActivity.transactionToFragment(new NotificationAndSoundFragment());
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
                            mainActivity.transactionToFragment(new MapFeaturesFragment());
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
                            mainActivity.transactionToFragment(new LanguageFragment());
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
