// Generated by view binder compiler. Do not edit!
package com.example.screenrecorderv2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewbinding.ViewBinding;
import com.airbnb.lottie.LottieAnimationView;
import com.example.screenrecorderv2.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySplashBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LottieAnimationView imgCongratulations;

  @NonNull
  public final AppCompatTextView tv;

  @NonNull
  public final AppCompatTextView tvStudio;

  private ActivitySplashBinding(@NonNull LinearLayout rootView,
      @NonNull LottieAnimationView imgCongratulations, @NonNull AppCompatTextView tv,
      @NonNull AppCompatTextView tvStudio) {
    this.rootView = rootView;
    this.imgCongratulations = imgCongratulations;
    this.tv = tv;
    this.tvStudio = tvStudio;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySplashBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySplashBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_splash, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySplashBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.img_congratulations;
      LottieAnimationView imgCongratulations = rootView.findViewById(id);
      if (imgCongratulations == null) {
        break missingId;
      }

      id = R.id.tv;
      AppCompatTextView tv = rootView.findViewById(id);
      if (tv == null) {
        break missingId;
      }

      id = R.id.tv_studio;
      AppCompatTextView tvStudio = rootView.findViewById(id);
      if (tvStudio == null) {
        break missingId;
      }

      return new ActivitySplashBinding((LinearLayout) rootView, imgCongratulations, tv, tvStudio);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
