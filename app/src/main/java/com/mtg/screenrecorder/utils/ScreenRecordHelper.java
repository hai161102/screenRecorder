package com.mtg.screenrecorder.utils;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.utils.media.MediaAudioEncoder;
import com.mtg.screenrecorder.utils.media.MediaEncoder;
import com.mtg.screenrecorder.utils.media.MediaMuxerWrapper;
import com.mtg.screenrecorder.utils.media.MediaScreenEncoder;

public class ScreenRecordHelper {
    public static State STATE = State.STOPED;

    private Context context;
    private CallBackRecordHelper callBackRecordHelper;
    private DisplayMetrics metrics;
    private int screenOrientation;
    private int widthVideo, heightVideo, fpsVideo, bitrateVideo;
    private Storage.VideoValue videoValue;
    private int time = 0;
    private MediaMuxerWrapper mMuxer;

    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaProjection mMediaProjection;
    private static final Object sSync = new Object();

    public int getTime() {
        return time;
    }

    public ScreenRecordHelper(Context context, DisplayMetrics metrics, CallBackRecordHelper callBackRecordHelper) {
        this.context = context;
        this.metrics = metrics;
        this.callBackRecordHelper = callBackRecordHelper;
        init();
    }

    private void init() {
        screenOrientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        getValues();
    }

    private Handler handler = new Handler();
    private Runnable runnableTime = new Runnable() {
        @Override
        public void run() {
            if (STATE == State.RECORDING) {
                time++;
                callBackRecordHelper.onTimeRun(time);
            }
            handler.postDelayed(runnableTime, 1000);
        }
    };

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            stopScreenRecording();
        }
    }

    public State togglePausePlay() {
        if (STATE == State.RECORDING) {
            pauseScreenRecording();
        } else if (STATE == State.PAUSED) {
            resumeScreenRecording();
        }
        return STATE;
    }

    public void getValues() {
        Resolution res = getResolution();
        setOrientation(res);
        fpsVideo = Integer.parseInt(PreferencesHelper.getString(PreferencesHelper.KEY_FRAMES, Config.itemsFrame[0].getValue()));
        bitrateVideo = Integer.parseInt(PreferencesHelper.getString(PreferencesHelper.KEY_BIT_RATE, Config.itemsBitRate[3].getValue()));
        videoValue = Storage.saveVideo(context, false);
    }

    //Get the device resolution in pixels
    private Resolution getResolution() {
        int width = Integer.parseInt(PreferencesHelper.getString(PreferencesHelper.KEY_RESOLUTION, Config.itemsResolution[1].getValue()));
        for (Resolution resolution : Resolution.values()) {
            if (resolution.width == width) {
                return resolution;
            }
        }
        return Resolution.RES_2;
    }

    private void setOrientation(Resolution res) {
        String orientationPrefs = PreferencesHelper.getString(PreferencesHelper.KEY_ORIENTATION, Config.itemsOrientation[0].getValue());
        String defaultResolution = PreferencesHelper.getString(PreferencesHelper.KEY_DEFAULT_RESOLUTION, "1080x1920");
        String[] outSize = defaultResolution.split("x");
        float defaultWidth = Float.parseFloat(outSize[0]);
        float defaultHeight= Float.parseFloat(outSize[1]);
        float density = defaultWidth/res.width;
        switch (orientationPrefs) {
            case Config.ORIENTATION_AUTO:
                if (screenOrientation == 0 || screenOrientation == 2) {
                    widthVideo = (int) (defaultWidth/density);
                    heightVideo = (int) (defaultHeight/density);
                } else {
                    heightVideo = (int) (defaultWidth/density);
                    widthVideo = (int) (defaultHeight/density);
                }
                break;
            case Config.ORIENTATION_PORTRAIT:
                widthVideo = (int) (defaultWidth/density);
                heightVideo = (int) (defaultHeight/density);
                break;
            case Config.ORIENTATION_LANDSCAPE:
                heightVideo = (int) (defaultWidth/density);
                widthVideo = (int) (defaultHeight/density);
                break;
        }
    }


    private static final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
        }
    };

    public void startRecording(int mResultCode, Intent mResultData) {
        try {
            MediaProjectionManager mProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mMediaProjectionCallback = new MediaProjectionCallback();
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode, mResultData);
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);

            mMuxer = new MediaMuxerWrapper(".mp4", videoValue.getPath());    // if you record audio only, ".m4a" is also OK.
            new MediaScreenEncoder(mMuxer, mMediaEncoderListener, mMediaProjection, widthVideo, heightVideo, metrics.densityDpi, bitrateVideo, fpsVideo);
            if (!PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE).equals(Config.AUDIO_NONE)) {
                new MediaAudioEncoder(mMuxer, mMediaProjection, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
            STATE = State.RECORDING;
            handler.post(runnableTime);
            Toast.makeText(context, context.getString(R.string.start_record), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            destroyMediaProjection();
            handler.removeCallbacksAndMessages(null);
            STATE = State.STOPED;
            if (callBackRecordHelper != null) {
                callBackRecordHelper.onError();
            }
            Toast.makeText(context, context.getString(R.string.record_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseScreenRecording() {
        synchronized (sSync) {
            if (mMuxer != null) {
                mMuxer.pauseRecording();
            }
            STATE = State.PAUSED;
        }
    }

    public void resumeScreenRecording() {
        synchronized (sSync) {
            if (mMuxer != null) {
                mMuxer.resumeRecording();
            }
            STATE = State.RECORDING;
        }
    }

    public void stopScreenRecording() {
        destroyMediaProjection();
        if (callBackRecordHelper != null) {
            callBackRecordHelper.onStopScreenRecording(videoValue.getPath());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Storage.updateGalleryAbove10(context, videoValue.getContentValues(), videoValue.getUri());
        } else {
            Storage.updateGalleryBelow10(context, videoValue.getPath());
        }
        STATE = State.STOPED;
    }

    private void destroyMediaProjection() {
        try {
            synchronized (sSync) {
                if (mMuxer != null) {
                    mMuxer.stopRecording();
                }
            }
            if (mMediaProjection != null) {
                mMediaProjection.unregisterCallback(mMediaProjectionCallback);
                mMediaProjection.stop();
                mMediaProjection = null;
            }
        } catch (Exception e) {
            Log.e("TAG", "destroyMediaProjection :" + e.getMessage());
        }
    }

    public interface CallBackRecordHelper {
        void onTimeRun(int time);

        void onStopScreenRecording(String dstPath);

        void onError();
    }

    public enum State {
        RECORDING, PAUSED, STOPED
    }

    public enum Resolution {

        RES_1(360),
        RES_2(720),
        RES_3(1080),
        RES_4(1440);

        private int width;

        Resolution(int width) {
            this.width = width;
        }
    }
}
