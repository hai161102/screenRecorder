package com.example.screenrecorderv2.service.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.ads.control.AdmobHelp;
import com.bumptech.glide.Glide;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.service.base.BaseLayoutWindowManager;
import com.example.screenrecorderv2.ui.trimvideo.VideoTrimActivity;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.ViewUtils;
import com.example.screenrecorderv2.utils.notification.ServiceNotificationManager;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class LayoutPreviewMediaManager extends BaseLayoutWindowManager {
    private String filePath;
    private Uri fileUri;
    private boolean isVideo;

    public LayoutPreviewMediaManager(Context context, String path) {
        super(context);
        this.filePath = path;
        initParams();
        addLayout();
        initData();
    }

    private void initParams() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_preview_media_file;
    }

    @Override
    protected void initLayout() {
        AdmobHelp.getInstance().loadNativeWindow(context, rootView);
        rootView.findViewById(R.id.imv_close).setOnClickListener(v -> removeLayout());
        rootView.findViewById(R.id.imv_play).setOnClickListener(v -> openMedia());
        rootView.findViewById(R.id.tv_share).setOnClickListener(v -> shareMedia());
        rootView.findViewById(R.id.tv_edit).setOnClickListener(v -> openEditVideo());
        rootView.findViewById(R.id.tv_delete).setOnClickListener(v -> deleteMedia());
        rootView.findViewById(R.id.thumbnail).setOnClickListener(v -> openMedia());
        ViewUtils.scaleSelected(rootView.findViewById(R.id.imv_close), rootView.findViewById(R.id.imv_play), rootView.findViewById(R.id.tv_share), rootView.findViewById(R.id.tv_edit), rootView.findViewById(R.id.tv_delete));
    }

    private void deleteMedia() {
        ServiceNotificationManager.getInstance(context).hideScreenRecordSuccessNotification();
        ServiceNotificationManager.getInstance(context).hideScreenshotSuccessNotification();
        RxBusHelper.sendNotiMediaChange();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        removeLayout();
    }

    private void openEditVideo() {
        removeLayout();
        Intent intent = new Intent(context, VideoTrimActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.EXTRA_PATH, filePath);
        context.startActivity(intent);
    }

    private void shareMedia() {
        removeLayout();
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, fileUri)
                .setType(isVideo ? "video/mp4" : "image/*");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    private void openMedia() {
        removeLayout();
        try {
            Intent openVideoIntent = new Intent();
            openVideoIntent.setAction(Intent.ACTION_VIEW)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(
                            fileUri,
                            context.getContentResolver().getType(fileUri));
            context.startActivity(openVideoIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            if (filePath.endsWith(".mp4")) {
                isVideo = true;
            } else {
                isVideo = false;
                rootView.findViewById(R.id.imv_play).setVisibility(View.GONE);
                rootView.findViewById(R.id.tv_edit).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.tv_title)).setText(context.getString(R.string.screenshot_finished));
            }
            fileUri = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".provider",
                    new File(filePath));
//            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
//            mmr.setDataSource(filePath);
//            Bitmap bitmap = mmr.getFrameAtTime(1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            Glide.with(context).load(filePath).into((ImageView) rootView.findViewById(R.id.thumbnail));
        } catch (Exception e) {
            Log.d("TAG", "initData: " + e.getMessage());
        }
    }
}
