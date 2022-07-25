package com.example.screenrecorderv2.utils.media;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: MediaAudioEncoder.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
 */

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaAudioEncoder extends MediaEncoder {
    private static final boolean DEBUG = false;    // TODO set false on release
    private static final String TAG = MediaAudioEncoder.class.getSimpleName();

    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;    // 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int BIT_RATE = 64000;
    public static final int SAMPLES_PER_FRAME = 1024;    // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec

    private AudioThread mAudioThread = null;
    private AudioThread2 mAudioThread2 = null;
    private MediaProjection mMediaProjection;

    public MediaAudioEncoder(final MediaMuxerWrapper muxer, final MediaProjection projection, final MediaEncoderListener listener) {
        super(muxer, listener);
        mMediaProjection = projection;
    }

    @Override
    protected void prepare() throws IOException {
        if (DEBUG) Log.v(TAG, "prepare:");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;
        // prepare MediaCodec for AAC encoding of audio data from inernal mic.
        final MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        if (DEBUG) Log.i(TAG, "selected codec: " + audioCodecInfo.getName());

        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        int chanelMask =
                PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_MIC).equals(Config.AUDIO_MIC_AND_INTERNAL)
                        ? AudioFormat.CHANNEL_IN_DEFAULT
                        : AudioFormat.CHANNEL_IN_MONO;
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, chanelMask);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
//		audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, inputFile.length());
//      audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );
        if (DEBUG) Log.i(TAG, "format: " + audioFormat);
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        if (DEBUG) Log.i(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }
    }

    @Override
    protected void startRecording() {
        super.startRecording();
        // create and execute audio capturing thread using internal mic
        if (mAudioThread == null) {
            mAudioThread = new AudioThread();
            mAudioThread.start();
        }
        if (mAudioThread2 == null
                && PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE).equals(Config.AUDIO_MIC_AND_INTERNAL)) {
            mAudioThread2 = new AudioThread2();
            mAudioThread2.start();
        }
    }

    @Override
    protected void release() {
        mAudioThread = null;
        mAudioThread2 = null;
        super.release();
    }

    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.MIC,
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private AudioRecord createAudioRecordPlayback(int bufferSize) {
        AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mMediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .build();
        /**
         * Using hardcoded values for the audio format, Mono PCM samples with a sample rate of 8000Hz
         * These can be changed according to your application's needs
         */
        int chanelMask =
                PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE).equals(Config.AUDIO_MIC_AND_INTERNAL)
                        ? AudioFormat.CHANNEL_IN_DEFAULT
                        : AudioFormat.CHANNEL_IN_MONO;
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(chanelMask)
                .build();

        AudioRecord audioRecord = new AudioRecord.Builder()
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setAudioPlaybackCaptureConfig(config)
                .build();
        return audioRecord;
    }

    private AudioRecord createAudioRecordMic(int bufferSize) {
        AudioRecord audioRecord = null;
        int chanelMask =
                PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE).equals(Config.AUDIO_MIC_AND_INTERNAL)
                        ? AudioFormat.CHANNEL_IN_DEFAULT
                        : AudioFormat.CHANNEL_IN_MONO;
        try {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    chanelMask, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                audioRecord.release();
                audioRecord = null;
            }
        } catch (final Exception e) {
            audioRecord = null;
        }
        return audioRecord;
    }

    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            String audioType = PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE);
            if (!audioType.equals(Config.AUDIO_NONE)) {
                try {
                    final int min_buffer_size = AudioRecord.getMinBufferSize(
                            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
                    if (buffer_size < min_buffer_size)
                        buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

                    AudioRecord audioRecord = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        switch (audioType) {
                            case Config.AUDIO_INTERNAL:
                                audioRecord = createAudioRecordPlayback(buffer_size);
                                break;
                            case Config.AUDIO_MIC_AND_INTERNAL:
                                audioRecord = createAudioRecordPlayback(buffer_size);
                                break;
                            case Config.AUDIO_MIC:
                                audioRecord = createAudioRecordMic(buffer_size);
                                break;
                        }
                    } else {
                        if (audioType.equals(Config.AUDIO_MIC)) {
                            audioRecord = createAudioRecordMic(buffer_size);
                        } else {
                            frameAvailableSoon();
                        }
                    }

                    if (audioRecord != null) {
                        try {
                            if (mIsCapturing) {
                                if (DEBUG) Log.v(TAG, "AudioThread:start audio recording");
                                final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                                int readBytes;
                                audioRecord.startRecording();
                                try {
                                    for (; mIsCapturing && !mRequestStop && !mIsEOS; ) {
                                        // read audio data from internal mic
                                        buf.clear();
                                        readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                        if (readBytes > 0) {
                                            // set audio data to encoder
                                            buf.position(readBytes);
                                            buf.flip();
                                            encode(buf, readBytes, getPTSUs());
                                            frameAvailableSoon();
                                        }
                                    }
                                    frameAvailableSoon();
                                } finally {
                                    audioRecord.stop();
                                }
                            }
                        } finally {
                            audioRecord.release();
                        }
                    } else {
                        Log.e(TAG, "failed to initialize AudioRecord");
                    }
                } catch (final Exception e) {
                    Log.e(TAG, "AudioThread#run", e);
                }
            } else {
                frameAvailableSoon();
            }
            if (DEBUG) Log.v(TAG, "AudioThread:finished");
        }
    }

    private class AudioThread2 extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                final int min_buffer_size = AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
                if (buffer_size < min_buffer_size)
                    buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;
                AudioRecord audioRecord = createAudioRecordMic(buffer_size);
                if (audioRecord != null) {
                    try {
                        if (mIsCapturing) {
                            final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                            int readBytes;
                            audioRecord.startRecording();
                            try {
                                while (mIsCapturing && !mRequestStop && !mIsEOS) {
                                    // read audio data from internal mic
                                    buf.clear();
                                    readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                    if (readBytes > 0) {
                                        // set audio data to encoder
                                        buf.position(readBytes);
                                        buf.flip();
                                        encode(buf, readBytes, getPTSUs());
                                        frameAvailableSoon();
                                    }
                                }
                                frameAvailableSoon();
                            } finally {
                                audioRecord.stop();
                            }
                        }
                    } finally {
                        audioRecord.release();
                    }
                } else {
                    Log.e(TAG, "failed to initialize AudioRecord");
                }
            } catch (final Exception e) {
                Log.e(TAG, "AudioThread#run", e);
            }
        }
    }

    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return
     */
    private static final MediaCodecInfo selectAudioCodec(final String mimeType) {
        if (DEBUG) Log.v(TAG, "selectAudioCodec:");

        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (DEBUG) Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break LOOP;
                    }
                }
            }
        }
        return result;
    }

}
