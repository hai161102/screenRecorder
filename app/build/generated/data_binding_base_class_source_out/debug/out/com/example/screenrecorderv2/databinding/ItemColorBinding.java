// Generated by view binder compiler. Do not edit!
package com.example.screenrecorderv2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.example.screenrecorderv2.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemColorBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final View imvCheck;

  @NonNull
  public final LinearLayout imvColor;

  private ItemColorBinding(@NonNull ConstraintLayout rootView, @NonNull View imvCheck,
      @NonNull LinearLayout imvColor) {
    this.rootView = rootView;
    this.imvCheck = imvCheck;
    this.imvColor = imvColor;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemColorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemColorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_color, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemColorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imv_check;
      View imvCheck = rootView.findViewById(id);
      if (imvCheck == null) {
        break missingId;
      }

      id = R.id.imv_color;
      LinearLayout imvColor = rootView.findViewById(id);
      if (imvColor == null) {
        break missingId;
      }

      return new ItemColorBinding((ConstraintLayout) rootView, imvCheck, imvColor);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
