package com.example.mobv2.ui.view.navigationDrawer;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobv2.R;
import com.example.mobv2.databinding.FragmentMainBinding;
import com.example.mobv2.databinding.NavHeaderMainBinding;
import com.example.mobv2.model.MenuItemMetadatum;
import com.example.mobv2.model.UserImpl;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;
import com.example.mobv2.ui.activity.mainActivity.MainActivityViewModel;
import com.example.mobv2.ui.fragment.LanguageFragment;
import com.example.mobv2.ui.fragment.MapFeaturesFragment;
import com.example.mobv2.ui.fragment.NotificationAndSoundFragment;
import com.example.mobv2.ui.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class NavDrawer implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int PROFILE_GROUP = 0, SETTINGS_GROUP = 1;

    private final UserImpl user;

    private final MainActivity mainActivity;

    private final DrawerLayout drawerLayout;
    private View headerView;

    private FragmentMainBinding binding;

    public NavDrawer(MainActivity mainActivity,
                     FragmentMainBinding binding)
    {
        this.mainActivity = mainActivity;
        this.binding = binding;

        var currentUser = mainActivity.appDatabase.userDao().getCurrentOne();
        user = currentUser != null ? currentUser : new UserImpl();

        drawerLayout = mainActivity.findViewById(R.id.drawer_layout);

        initNavigationView();
        updateHeaderView();
        initNavigationMenu();
    }

    private void initNavigationView()
    {
        var navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        headerView = navigationView.getHeaderView(0);

        var binding = NavHeaderMainBinding.bind(headerView);
        var mainActivityViewModel = new ViewModelProvider(mainActivity).get(
                MainActivityViewModel.class);
        binding.setBindingContext(mainActivityViewModel);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        var viewModel = new ViewModelProvider(mainActivity.getFragmentAtFrame()).get(
                NavDrawerViewModel.class);
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

    private void updateHeaderView()
    {
        var avatarView = (ImageView) headerView.findViewById(R.id.avatar_view);

        MainActivity.loadImageInView(user.getAvatarUrl(), binding.navigationView, avatarView);
    }

    private void initNavigationMenu()
    {
        var menu = binding.navigationView.getMenu();
        menu.clear();

        int order = 0;

        addNavigationMenuItem(menu, PROFILE_GROUP, order++, R.string.menu_profile,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT, () ->
                {
                    mainActivity.goToFragment(new ProfileFragment());
                }), R.drawable.ic_menu_profile);

        // the next submenu
        Menu submenuSettings = menu.addSubMenu(SETTINGS_GROUP, Menu.NONE, order,
                R.string.nav_header_item_settings);

        addNavigationMenuItem(submenuSettings, SETTINGS_GROUP, order++,
                R.string.menu_notification_and_sound,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT, () ->
                {
                    mainActivity.goToFragment(new NotificationAndSoundFragment());
                }), R.drawable.ic_menu_notification);

        addNavigationMenuItem(submenuSettings, SETTINGS_GROUP, order++, R.string.menu_map_features,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT, () ->
                {
                    mainActivity.goToFragment(new MapFeaturesFragment());
                }), R.drawable.ic_menu_map);

        addNavigationMenuItem(submenuSettings, SETTINGS_GROUP, order, R.string.menu_language,
                new MenuItemMetadatum(MenuItemMetadatum.ITEM_FRAGMENT, () ->
                {
                    mainActivity.goToFragment(new LanguageFragment());
                }), R.drawable.ic_menu_international);
    }


    private void addNavigationMenuItem(Menu menu,
                                       int groupId,
                                       int order,
                                       @StringRes int title,
                                       MenuItemMetadatum menuItemMetadatum,
                                       @DrawableRes int icon)
    {
        addNavigationMenuItem(menu, groupId, order, mainActivity.getString(title),
                menuItemMetadatum, icon);
    }

    private void addNavigationMenuItem(Menu menu,
                                       int groupId,
                                       int order,
                                       CharSequence title,
                                       MenuItemMetadatum menuItemMetadatum,
                                       @DrawableRes int icon)
    {
        var menuItem = menu.add(groupId, order, order, title).setIcon(icon);

        var viewModel = new ViewModelProvider(mainActivity.getFragmentAtFrame()).get(
                NavDrawerViewModel.class);

        viewModel.putMenuItemHash(menuItem, menuItemMetadatum);
    }

    public boolean isOpen()
    {
        return drawerLayout.isDrawerOpen(binding.navigationView);
    }

    public void open()
    {
        drawerLayout.openDrawer(binding.navigationView);
    }

    public boolean isClosed()
    {
        return !drawerLayout.isDrawerOpen(binding.navigationView);
    }

    public void close()
    {
        drawerLayout.closeDrawer(binding.navigationView);
    }
}
