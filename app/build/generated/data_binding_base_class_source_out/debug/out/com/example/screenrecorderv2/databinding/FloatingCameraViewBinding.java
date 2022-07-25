// Generated by view binder compiler. Do not edit!
package com.example.screenrecorderv2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.example.screenrecorderv2.R;
import com.master.cameralibrary.CameraView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FloatingCameraViewBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final CameraView cameraView;

  @NonNull
  public final ImageView imvHideCamera;

  @NonNull
  public final ImageView imvOverlayResize;

  @NonNull
  public final ImageView imvSwitchCamera;

  private FloatingCameraViewBinding(@NonNull LinearLayout rootView, @NonNull CameraView cameraView,
      @NonNull ImageView imvHideCamera, @NonNull ImageView imvOverlayResize,
      @NonNull ImageView imvSwitchCamera) {
    this.rootView = rootView;
    this.cameraView = cameraView;
    this.imvHideCamera = imvHideCamera;
    this.imvOverlayResize = imvOverlayResize;
    this.imvSwitchCamera = imvSwitchCamera;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FloatingCameraViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FloatingCameraViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.floating_camera_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FloatingCameraViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cameraView;
      CameraView cameraView = rootView.findViewById(id);
      if (cameraView == null) {
        break missingId;
      }

      id = R.id.imv_hide_camera;
      ImageView imvHideCamera = rootView.findViewById(id);
      if (imvHideCamera == null) {
        break missingId;
      }

      id = R.id.imv_overlay_resize;
      ImageView imvOverlayResize = rootView.findViewById(id);
      if (imvOverlayResize == null) {
        break missingId;
      }

      id = R.id.imv_switch_camera;
      ImageView imvSwitchCamera = rootView.findViewById(id);
      if (imvSwitchCamera == null) {
        break missingId;
      }

      return new FloatingCameraViewBinding((LinearLayout) rootView, cameraView, imvHideCamera,
          imvOverlayResize, imvSwitchCamera);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
