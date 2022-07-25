// Generated by view binder compiler. Do not edit!
package com.example.screenrecorderv2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.ViewPager;
import com.example.screenrecorderv2.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final View layoutBanner;

  @NonNull
  public final ViewPager viewpager;

  private ContentMainBinding(@NonNull ConstraintLayout rootView, @NonNull View layoutBanner,
      @NonNull ViewPager viewpager) {
    this.rootView = rootView;
    this.layoutBanner = layoutBanner;
    this.viewpager = viewpager;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.layout_banner;
      View layoutBanner = rootView.findViewById(id);
      if (layoutBanner == null) {
        break missingId;
      }

      id = R.id.viewpager;
      ViewPager viewpager = rootView.findViewById(id);
      if (viewpager == null) {
        break missingId;
      }

      return new ContentMainBinding((ConstraintLayout) rootView, layoutBanner, viewpager);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
