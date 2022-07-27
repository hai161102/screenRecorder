package com.mtg.screenrecorder.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.mtg.screenrecorder.base.BaseRecyclerAdapter;
import com.mtg.screenrecorder.base.BaseViewHolder;
import com.mtg.screenrecorder.databinding.ItemDateBinding;
import com.mtg.screenrecorder.databinding.ItemVideosBinding;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.trimlib.VideoFile;
import com.mtg.screenrecorder.utils.Toolbox;
import com.mtg.screenrecorder.view.activity.VideoTrimActivity;

import java.io.File;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoAdapter extends BaseRecyclerAdapter<VideoFile> {
    private static final int ITEM_HEADER = 951;
    private static final int ITEM_VIDEO = 952;
    private CallBackVideo callBackVideo;
    private Handler handler;
    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

    public void setCallBackVideo(CallBackVideo callBackVideo) {
        this.callBackVideo = callBackVideo;
    }

    public VideoAdapter(List<VideoFile> list, Context context, Handler handler) {
        super(list, context);
        this.handler = handler;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isHeader()) {
            return ITEM_HEADER;
        }
        return ITEM_VIDEO;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_HEADER:
                return new HeaderViewHolder(ItemDateBinding.inflate(LayoutInflater.from(context)));
            case ITEM_VIDEO:
                return new VideoViewHolder(ItemVideosBinding.inflate(LayoutInflater.from(context)));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        VideoFile item = list.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).binding.tvDate.setText(Toolbox.getDateString(item.getLastModified()));
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).binding.tvDuration.setText(Toolbox.convertDuration(item.getDuration()));
            ((VideoViewHolder) holder).binding.tvName.setText(item.getName());
            ((VideoViewHolder) holder).binding.tvResolution.setText(item.getResolution());
            ((VideoViewHolder) holder).binding.tvSize.setText(Toolbox.formatSize(item.getSize()));
            Glide.with(context).load(item.getPath()).into(((VideoViewHolder) holder).binding.imvVideo);
            ((VideoViewHolder) holder).binding.getRoot().setOnClickListener(v -> callBackVideo.onClickItem(item));
            ((VideoViewHolder) holder).binding.imvMore.setOnClickListener(v -> callBackVideo.onCLickMore(item, holder.getAdapterPosition(), v));
            ((VideoViewHolder) holder).binding.shareItem.setOnClickListener(v -> {
                shareMedia(item.getPath(), v.getContext());
            });
            ((VideoViewHolder) holder).binding.editItem.setOnClickListener(v -> {
                openEditVideo(item.getPath(), v.getContext());
            });
        }
    }

    public class HeaderViewHolder extends BaseViewHolder<ItemDateBinding> {

        public HeaderViewHolder(ItemDateBinding binding) {
            super(binding);
        }
    }

    public class VideoViewHolder extends BaseViewHolder<ItemVideosBinding> {

        public VideoViewHolder(ItemVideosBinding binding) {
            super(binding);
        }
    }

    public interface CallBackVideo {
        void onClickItem(VideoFile item);

        void onCLickMore(VideoFile item, int pos, View view);
    }

    private void shareMedia(String filePath, Context context) {
        Uri fileUri = FileProvider.getUriForFile(
                context, context.getPackageName() + ".provider",
                new File(filePath));
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, fileUri)
                .setType(filePath.endsWith(".mp4") ? "video/mp4" : "image/*");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }
    private void openEditVideo(String filePath, Context context) {
        Intent intent = new Intent(context, VideoTrimActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.EXTRA_PATH, filePath);
        context.startActivity(intent);
    }
}
