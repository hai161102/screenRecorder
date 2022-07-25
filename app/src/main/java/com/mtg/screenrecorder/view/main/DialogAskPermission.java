package com.mtg.screenrecorder.view.main;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.databinding.DialogAskPermissionBinding;

public class DialogAskPermission extends DialogFragment {

    private SuccessListener mSuccessListener;
    private String permissionName;
    private DialogAskPermissionBinding binding;

    public interface SuccessListener {
        void onSuccess();
    }

    public static DialogAskPermission getInstance(String permissionName, SuccessListener mSuccessListener) {
        DialogAskPermission mDialogAskPermission = new DialogAskPermission();
        mDialogAskPermission.mSuccessListener = mSuccessListener;
        mDialogAskPermission.permissionName = permissionName;
        return mDialogAskPermission;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        String t = getClass().getSimpleName();
        if (manager.findFragmentByTag(t) == null) {
            super.show(manager, t);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        binding = DialogAskPermissionBinding.inflate(LayoutInflater.from(getContext()));
        dialogBuilder.setView(binding.getRoot());
        initData();
        return dialogBuilder.create();
    }

    private void initData() {
        try {
            switch (permissionName) {
                case Manifest.permission.RECORD_AUDIO:
                    binding.tvContent.setText(getString(R.string.record_audio_permission));
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    binding.tvContent.setText(getString(R.string.external_storage_permission));
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    binding.tvContent.setText(getString(R.string.external_storage_permission));
                    break;
                case Settings.ACTION_MANAGE_OVERLAY_PERMISSION:
                    binding.tvContent.setText(getString(R.string.overlay_permission));
                    break;
            }
            binding.tvAction1.setOnClickListener(v -> {
                dismiss();
                if (mSuccessListener != null)
                    mSuccessListener.onSuccess();
            });
        } catch (Exception e) {
            dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.7f;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
//            windowParams.windowAnimations = R.style.DialogAnimation;
            window.setAttributes(windowParams);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
