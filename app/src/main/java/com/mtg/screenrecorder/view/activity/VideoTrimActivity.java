package com.mtg.screenrecorder.view.activity;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.BaseActivity;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.databinding.ActivityVideoTrimBinding;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.Storage;
import com.mtg.screenrecorder.utils.Toolbox;
import com.mtg.screenrecorder.utils.trimlib.BackgroundTask;
import com.mtg.screenrecorder.utils.trimlib.OnVideoTrimListener;
import com.mtg.screenrecorder.utils.trimlib.TrimVideoUtils;
import com.mtg.screenrecorder.view.dialog.LoadingDialog;

import java.io.File;
import java.io.IOException;

import idv.luchafang.videotrimmer.VideoTrimmerView;


public class VideoTrimActivity extends BaseActivity<ActivityVideoTrimBinding> implements VideoTrimmerView.OnSelectedRangeChangedListener {
    private LoadingDialog loadingDialog;
    private long mStartPosition = 0;
    private long mEndPosition = 0;

    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    String videoPath;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    if (player.getCurrentPosition() + 1000 >= mEndPosition) {
                        player.setPlayWhenReady(false);
                    }
                }
            }
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected ActivityVideoTrimBinding getViewBinding() {
        return ActivityVideoTrimBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
//        AdmobHelp.getInstance().showInterstitialAd(this, () -> {
//
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Toolbox.getHeightStatusBar(this) > 0) {
            binding.container.setPadding(0, Toolbox.getHeightStatusBar(this), 0, 0);
        }
    }

    @Override
    protected void initControl() {
        if (getIntent().getExtras() != null) {
            videoPath = getIntent().getExtras().getString(Config.EXTRA_PATH);
        }
        player = ExoPlayerFactory.newSimpleInstance(this);
        player.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_ALL);
        binding.playerView.setPlayer(player);
        dataSourceFactory = new DefaultDataSourceFactory(this, "VideoTrimmer");
        new Handler().postDelayed(() -> {
            boolean isShow = binding.videoTrimmerView
                    .setVideo(new File(videoPath))
                    .setMaxDuration(120_000)
                    .setMinDuration(3_000)
                    .setFrameCountInWindow(8)
                    .setExtraDragSpace(Toolbox.convertDpToPx(this, 2f))
                    .setOnSelectedRangeChangedListener(VideoTrimActivity.this)
                    .show();
            if (!isShow) {
                toast(getString(R.string.cant_trim_video));
                finish();
            }
        }, 1000);
        binding.imvBack.setOnClickListener(v -> finish());
        binding.imvPlay.setOnClickListener(v -> {
            if (player != null) {
                player.seekTo(mStartPosition);
                player.setPlayWhenReady(true);
            }
        });
        binding.tvTrim.setOnClickListener(v -> {
            long diff = mEndPosition - mStartPosition;
            if (diff < 3) {
            } else if (Uri.parse(videoPath) != null) {
                MediaMetadataRetriever
                        mediaMetadataRetriever = new MediaMetadataRetriever();
                try {
                    mediaMetadataRetriever.setDataSource(VideoTrimActivity.this, Uri.parse(videoPath));
                } catch (Exception e) {
                    Log.e("tvTrim onclick error: ", e.getMessage());
                }
                final File file = new File(videoPath);
                Storage.VideoValue videoValue = Storage.saveVideo(VideoTrimActivity.this, true);
                BackgroundTask.execute(
                        new BackgroundTask.Task("", 0L, "") {
                            @Override
                            public void execute() {
                                try {
                                    TrimVideoUtils.startTrim(file, videoValue.getPath(), mStartPosition, mEndPosition, new OnVideoTrimListener() {
                                        @Override
                                        public void onTrimStarted() {
                                            runOnUiThread(() -> {
                                                loadingDialog = new LoadingDialog(VideoTrimActivity.this);
                                                loadingDialog.show();
                                            });
                                        }

                                        @Override
                                        public void getResult(Uri uri) {
                                            runOnUiThread(() -> {
                                                toast(R.string.trim_video_success);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                    Storage.updateGalleryAbove10(VideoTrimActivity.this, videoValue.getContentValues(), videoValue.getUri());
                                                } else {
                                                    Storage.updateGalleryBelow10(VideoTrimActivity.this, videoValue.getPath());
                                                }
                                                RxBusHelper.sendTrimVideoSuccess();
                                                if (loadingDialog != null)
                                                    loadingDialog.dismiss();
                                                finish();
                                            });
                                        }

                                        @Override
                                        public void cancelAction() {
                                            runOnUiThread(() -> {
                                                if (loadingDialog != null)
                                                    loadingDialog.dismiss();
                                            });
                                        }

                                        @Override
                                        public void onError(String message) {
                                            runOnUiThread(() -> {
                                                if (loadingDialog != null)
                                                    loadingDialog.dismiss();
                                                toast(R.string.trim_video_failed);
                                            });
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> {
                                        if (loadingDialog != null)
                                            loadingDialog.dismiss();
                                        toast(R.string.trim_video_failed);
                                    });
                                }
                            }
                        }
                );
            }
        });
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    binding.imvPlay.setVisibility(View.GONE);
                } else {
                    binding.imvPlay.setVisibility(View.VISIBLE);
                }
            }
        });
        playVideo(videoPath);
        handler.post(runnable);
    }

    @Override
    public void onSelectRange(long startMillis, long endMillis) {
        if (mStartPosition != startMillis) {
            player.seekTo(startMillis);
        }
        mStartPosition = startMillis;
        mEndPosition = endMillis;
        showDuration(startMillis, endMillis);
    }

    @Override
    public void onSelectRangeEnd(long startMillis, long endMillis) {
        mStartPosition = startMillis;
        mEndPosition = endMillis;
        showDuration(startMillis, endMillis);
    }

    @Override
    public void onSelectRangeStart() {
        if (player == null) return;
        player.setPlayWhenReady(false);
    }

    private void showDuration(long startMillis, long endMillis) {
        binding.tvStartDuration.setText(Toolbox.convertToTime((int) (startMillis / 1000)));
        binding.tvEndDuration.setText(Toolbox.convertToTime((int) (endMillis / 1000)));
        long duration = (endMillis - startMillis) / 1000L;
        binding.durationView.setText(duration + " seconds selected");
    }

    private void playVideo(String path) {
        if (TextUtils.isEmpty(path)) return;
        if (player == null) return;
        ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(path));
        player.prepare(source);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
