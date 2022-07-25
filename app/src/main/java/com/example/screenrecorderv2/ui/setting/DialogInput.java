package com.example.screenrecorderv2.ui.setting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseDialog;
import com.example.screenrecorderv2.databinding.DialogInputBinding;

public class DialogInput extends Dialog {
    private CallBackDialog callBackDialog;

    public DialogInput(@NonNull Context context, String title, String input, CallBackDialog callBackDialog) {
        super(context, R.style.Theme_Dialog);
        this.callBackDialog = callBackDialog;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_input);
        ((AppCompatTextView) findViewById(R.id.tv_title)).setText(title);
        ((AppCompatEditText) findViewById(R.id.edt_input)).setText(input);
        ((AppCompatEditText) findViewById(R.id.edt_input)).setSelection(input.length());
        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_ok).setOnClickListener(v -> {
            if (callBackDialog != null) {
                if (!TextUtils.isEmpty(((AppCompatEditText) findViewById(R.id.edt_input)).getText().toString())) {
                    this.callBackDialog.onOK(((AppCompatEditText) findViewById(R.id.edt_input)).getText().toString());
                    dismiss();
                }
            }
        });
    }

    public interface CallBackDialog {
        void onOK(String input);
    }
}
