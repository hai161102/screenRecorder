package com.example.screenrecorderv2.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.screenrecorderv2.base.rx.RxBusHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;

public class ScreenShotHelper {
    private Context context;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private int mDensity;
    private Display mDisplay;
    private int mHeight;
    private int mWidth;

    public ScreenShotHelper(Context context, WindowManager windowManager, DisplayMetrics metrics) {
        this.context = context;
        initDisplay(windowManager, metrics);
    }

    private void initDisplay(WindowManager windowManager, DisplayMetrics metrics) {
        mDensity = metrics.densityDpi;
        mDisplay = windowManager.getDefaultDisplay();
    }

    public void captureScreen(Intent resultData, int resultCode) {
        if (!(resultCode == 0 || resultData == null)) {
            if (mMediaProjection != null) {
                tearDownMediaProjection();
            }
            mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mMediaProjection == null) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, resultData);
            }
            if (mMediaProjection != null) {
                setUpVirtualDisplay();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setUpVirtualDisplay() {
        Point point = new Point();
        mDisplay.getSize(point);
        mWidth = point.x;
        mHeight = point.y;
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("Screenrecorder", mWidth, mHeight, mDensity, 9, mImageReader.getSurface(), null, null);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        private ImageAvailableListener() {
        }

        public void onImageAvailable(ImageReader imageReader) {
            String filePath = null;
            Bitmap createBitmap = null;
            Image acquireLatestImage;
            try {
                acquireLatestImage = mImageReader.acquireLatestImage();
                if (acquireLatestImage != null) {
                    try {
                        Image.Plane[] planes = acquireLatestImage.getPlanes();
                        Buffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mWidth;
                        createBitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                        try {
                            createBitmap.copyPixelsFromBuffer(buffer);
                            if (acquireLatestImage != null) {
                                acquireLatestImage.close();
                            }
                            try {
                                filePath = Storage.saveImage(context, Toolbox.CropBitmapTransparency(Bitmap.createBitmap(createBitmap, 0, 0, mWidth, mHeight)));
                                acquireLatestImage.close();
                                stopScreenCapture();
                                tearDownMediaProjection();
                            } catch (Exception e) {
                                try {
                                    e.printStackTrace();
                                    if (imageReader != null) {
                                        try {
                                            imageReader.close();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    if (createBitmap != null) {
                                        createBitmap.recycle();
                                    }
                                    if (acquireLatestImage == null) {
                                        return;
                                    }
                                    acquireLatestImage.close();
                                } catch (Throwable th2) {
                                    if (createBitmap != null) {
                                        createBitmap.recycle();
                                    }
                                    if (acquireLatestImage != null) {
                                        acquireLatestImage.close();
                                    }
                                }
                            } catch (Throwable th3) {
                                if (createBitmap != null) {
                                    createBitmap.recycle();
                                }
                                if (acquireLatestImage != null) {
                                    acquireLatestImage.close();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            if (createBitmap != null) {
                                createBitmap.recycle();
                            }
                            if (acquireLatestImage == null) {
                                return;
                            }
                            acquireLatestImage.close();
                        }
                    } catch (Exception ex) {

                        createBitmap = null;
                        ex.printStackTrace();
                        if (createBitmap != null) {
                            createBitmap.recycle();
                        }
                        if (acquireLatestImage == null) {
                            return;
                        }
                        acquireLatestImage.close();
                    } catch (Throwable th4) {
                        createBitmap = null;
                        if (createBitmap != null) {
                            createBitmap.recycle();
                        }
                        if (acquireLatestImage != null) {
                            acquireLatestImage.close();
                        }
                    }
                }
                createBitmap = null;
                if (createBitmap != null) {
                    createBitmap.recycle();
                }
                if (acquireLatestImage != null) {
                    acquireLatestImage.close();
                }
            } catch (Exception ex) {
                acquireLatestImage = null;
                ex.printStackTrace();
                if (createBitmap != null) {
                    createBitmap.recycle();
                }
                if (acquireLatestImage == null) {
                    return;
                }
                acquireLatestImage.close();
            } finally {
                RxBusHelper.sendScreenShot(filePath);
            }
        }
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
            if (mImageReader != null) {
                mImageReader.setOnImageAvailableListener(null, null);
            }
        }
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
}
