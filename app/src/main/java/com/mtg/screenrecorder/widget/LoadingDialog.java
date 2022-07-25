package com.mtg.screenrecorder.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.mtg.screenrecorder.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
    }
}
