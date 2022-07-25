package com.example.screenrecorderv2.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.screenrecorderv2.R;
import com.google.android.material.tabs.TabLayout;

import static com.example.screenrecorderv2.utils.Toolbox.hideSoftKeyboard;


public class ViewUtils {

    public static void setupUI(final View view, final Activity context) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(context);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

    public static void setupUI(final View view, final Activity context, View... viewException) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(context);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                for (View aViewException : viewException) {
                    if (aViewException.getId() != innerView.getId()) {
                        setupUI(innerView, context);
                    }
                }
            }
        }
    }

    public static void loadImage(Context context, Object image, ImageView imageView) {
        Glide.with(context)
                .load(image)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    public static void scaleSelected(View... views) {
        for (View view : views) {
            view.setOnTouchListener((v, event) -> {
                v.setPivotX(v.getWidth() / 2);
                v.setPivotY(v.getHeight() / 2);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setScaleX(0.9f);
                        v.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setScaleX(1f);
                        v.setScaleY(1f);
                        break;
                }
                return false;
            });
        }
    }

    public static void hideOrShowTextViewInTab(TabLayout mTabLayout, boolean isHide) {
        int wantedTabIndex = 1;
        TextView tv = (TextView)(((ViewGroup)((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(wantedTabIndex)).getChildAt(1));
        tv.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }
}

