// Generated by view binder compiler. Do not edit!
package com.example.mob.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.mob.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentEditProfileBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AppBarLayout appBar;

  @NonNull
  public final FloatingActionButton fab;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final CollapsingToolbarLayout toolbarLayout;

  private FragmentEditProfileBinding(@NonNull LinearLayout rootView, @NonNull AppBarLayout appBar,
      @NonNull FloatingActionButton fab, @NonNull Toolbar toolbar,
      @NonNull CollapsingToolbarLayout toolbarLayout) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.fab = fab;
    this.toolbar = toolbar;
    this.toolbarLayout = toolbarLayout;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentEditProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentEditProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_edit_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentEditProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar;
      AppBarLayout appBar = ViewBindings.findChildViewById(rootView, id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.fab;
      FloatingActionButton fab = ViewBindings.findChildViewById(rootView, id);
      if (fab == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.toolbar_layout;
      CollapsingToolbarLayout toolbarLayout = ViewBindings.findChildViewById(rootView, id);
      if (toolbarLayout == null) {
        break missingId;
      }

      return new FragmentEditProfileBinding((LinearLayout) rootView, appBar, fab, toolbar,
          toolbarLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
