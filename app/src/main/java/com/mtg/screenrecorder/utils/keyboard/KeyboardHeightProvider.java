package com.mtg.screenrecorder.utils.keyboard;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

public class KeyboardHeightProvider extends PopupWindow {

    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;

    /**
     * The view that is used to calculate the keyboard height
     */
    private View popupView;

    /**
     * The parent view
     */
    private View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private Activity activity;


    @Nullable
    private Integer currentHeight;


    public int getCurrentHeight() {
        if (currentHeight == null) {
            return 0;
        }
        return currentHeight;
    }

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        this.popupView = new FrameLayout(activity);
        setContentView(popupView);

        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (popupView != null) {
                handleOnGlobalLayout();
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        this.observer = null;
        dismiss();
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
        handleOnGlobalLayout();
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        int keyboardHeight = screenSize.y - rect.bottom;

        if (currentHeight == null || currentHeight != keyboardHeight) {
            currentHeight = keyboardHeight;
            notifyKeyboardHeightChanged(keyboardHeight);
        }
    }

    /**
     * Notifies observer on every keyboard height change
     */
    private void notifyKeyboardHeightChanged(int height) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height);
        }
    }
}
