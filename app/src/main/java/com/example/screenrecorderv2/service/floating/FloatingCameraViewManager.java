package com.example.screenrecorderv2.service.floating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.master.cameralibrary.CameraView;

public class FloatingCameraViewManager {
    private final Context context;
    private LinearLayout mFloatingView;
    private ImageView imvResizeOverlay;
    private ImageView imvHideCamera;
    private ImageView imvSwitchCamera;
    private CameraView cameraView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private final Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            imvResizeOverlay.setVisibility(View.GONE);
            imvHideCamera.setVisibility(View.GONE);
            imvSwitchCamera.setVisibility(View.GONE);
        }
    };


    public FloatingCameraViewManager(Context context) {
        this.context = context;
        handler = new Handler();
        initLayout();
    }

    private void initLayout() {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFloatingView = (LinearLayout) li.inflate(R.layout.floating_camera_view, null);
        cameraView = mFloatingView.findViewById(R.id.cameraView);
        imvHideCamera = mFloatingView.findViewById(R.id.imv_hide_camera);
        imvSwitchCamera = mFloatingView.findViewById(R.id.imv_switch_camera);
        imvResizeOverlay = mFloatingView.findViewById(R.id.imv_overlay_resize);
        int xPos = getXPos();
        int yPos = getYPos();

        imvSwitchCamera.setOnClickListener(v -> {
            if (cameraView.getFacing() == CameraView.FACING_BACK) {
                cameraView.setFacing(CameraView.FACING_FRONT);
                cameraView.setAutoFocus(true);
            } else {
                cameraView.setFacing(CameraView.FACING_BACK);
                cameraView.setAutoFocus(true);
            }
        });

        imvHideCamera.setOnClickListener(v -> {
            onFinishFloatingView();
        });

        //Add the view to the window.
        params = new WindowManager.LayoutParams(
                (int) context.getResources().getDimension(R.dimen._136sdp),
                (int) context.getResources().getDimension(R.dimen._136sdp),
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_PHONE :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = xPos;
        params.y = yPos;

        //Add the view to the window
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        cameraView.start();
        setupDragListener();
    }

    private void setupDragListener() {
        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private final WindowManager.LayoutParams paramsF = params;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (imvResizeOverlay.isShown()) {
                            imvResizeOverlay.setVisibility(View.GONE);
                            imvHideCamera.setVisibility(View.GONE);
                            imvSwitchCamera.setVisibility(View.GONE);
                        } else {
                            imvResizeOverlay.setVisibility(View.VISIBLE);
                            imvHideCamera.setVisibility(View.VISIBLE);
                            imvSwitchCamera.setVisibility(View.VISIBLE);
                            handler.removeCallbacks(runnable);
                        }
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(runnable, 3000);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        int xDiff = (int) (event.getRawX() - initialTouchX);
                        int yDiff = (int) (event.getRawY() - initialTouchY);
                        paramsF.x = initialX + xDiff;
                        paramsF.y = initialY + yDiff;
                        /* Set an offset of 10 pixels to determine controls moving. Else, normal touches
                         * could react as moving the control window around */
                        if (Math.abs(xDiff) > 10 || Math.abs(yDiff) > 10)
                            mWindowManager.updateViewLayout(mFloatingView, paramsF);
                        persistCoordinates(initialX + xDiff, initialY + yDiff);
                        return true;
                }
                return false;
            }
        });

        imvResizeOverlay.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.width;
                        initialY = params.height;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(runnable, 3000);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (imvResizeOverlay.isShown()) {
                            handler.removeCallbacks(runnable);
                        }
                        params.width = (initialX + (int) (event.getRawX() - initialTouchX));
                        params.height = (initialY + (int) (event.getRawY() - initialTouchY));
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    public void onFinishFloatingView() {
        if (cameraView != null) {
            cameraView.stop();
            cameraView = null;
        }
        if (mFloatingView != null) {
            mWindowManager.removeViewImmediate(mFloatingView);
            mFloatingView = null;
        }
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false);
    }

    private int getXPos() {
        String pos = PreferenceManager.getDefaultSharedPreferences(context).getString("POSITION", "0X100");
        return Integer.parseInt(pos.split("X")[0]);
    }

    private int getYPos() {
        String pos = PreferenceManager.getDefaultSharedPreferences(context).getString("POSITION", "0X100");
        return Integer.parseInt(pos.split("X")[1]);
    }

    private void persistCoordinates(int x, int y) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("POSITION", x + "X" + y)
                .apply();
    }
}
