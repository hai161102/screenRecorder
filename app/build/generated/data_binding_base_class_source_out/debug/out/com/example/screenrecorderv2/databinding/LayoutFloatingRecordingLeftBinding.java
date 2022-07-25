// Generated by view binder compiler. Do not edit!
package com.example.screenrecorderv2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.example.screenrecorderv2.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutFloatingRecordingLeftBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final FrameLayout containerMain;

  @NonNull
  public final ImageView imvPause;

  @NonNull
  public final ImageView imvStop;

  @NonNull
  public final ImageView imvTools;

  @NonNull
  public final TextView tvTime;

  private LayoutFloatingRecordingLeftBinding(@NonNull ConstraintLayout rootView,
      @NonNull FrameLayout containerMain, @NonNull ImageView imvPause, @NonNull ImageView imvStop,
      @NonNull ImageView imvTools, @NonNull TextView tvTime) {
    this.rootView = rootView;
    this.containerMain = containerMain;
    this.imvPause = imvPause;
    this.imvStop = imvStop;
    this.imvTools = imvTools;
    this.tvTime = tvTime;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutFloatingRecordingLeftBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutFloatingRecordingLeftBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_floating_recording_left, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutFloatingRecordingLeftBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.container_main;
      FrameLayout containerMain = rootView.findViewById(id);
      if (containerMain == null) {
        break missingId;
      }

      id = R.id.imv_pause;
      ImageView imvPause = rootView.findViewById(id);
      if (imvPause == null) {
        break missingId;
      }

      id = R.id.imv_stop;
      ImageView imvStop = rootView.findViewById(id);
      if (imvStop == null) {
        break missingId;
      }

      id = R.id.imv_tools;
      ImageView imvTools = rootView.findViewById(id);
      if (imvTools == null) {
        break missingId;
      }

      id = R.id.tv_time;
      TextView tvTime = rootView.findViewById(id);
      if (tvTime == null) {
        break missingId;
      }

      return new LayoutFloatingRecordingLeftBinding((ConstraintLayout) rootView, containerMain,
          imvPause, imvStop, imvTools, tvTime);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
