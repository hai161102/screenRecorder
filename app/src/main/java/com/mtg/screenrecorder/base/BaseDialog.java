package com.mtg.screenrecorder.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

public abstract class BaseDialog<B extends ViewBinding> extends Dialog {
    protected B binding;

    public BaseDialog(@NonNull Context context) {
        super(context);
        initView();
        initData();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
        initData();
    }

    protected void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = getViewBinding();
        setContentView(binding.getRoot());
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        window.setLayout(((getWidth(getContext()) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        windowParams.dimAmount = 0.7f;
        window.setAttributes(windowParams);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public int getWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    protected abstract void initData();

    protected abstract B getViewBinding();
}
