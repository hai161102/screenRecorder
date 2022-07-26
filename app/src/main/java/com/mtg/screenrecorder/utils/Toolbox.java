package com.mtg.screenrecorder.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;


import com.mtg.screenrecorder.R;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.BuildConfig;

public class Toolbox {

    public static void setStatusBarHomeWhite(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.setNavigationBarColor(Color.BLACK);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //make fully Android Transparent Status bar
            setWindowFlag((AppCompatActivity) activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            window.setStatusBarColor(activity.getResources().getColor(R.color.white));
        }
    }

    public static void setWindowStatusDark(FragmentActivity activity, boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            if (isDark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // We want to change tint color to white again.
                // You can also record the flags in advance so that you can turn UI back completely if
                // you have set other flags before, such as translucent or full screen.
                decor.setSystemUiVisibility(0);
            }
        }
    }

    public static void setStatusBarHomeTransfer(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            //make fully Android Transparent Status bar
            setWindowFlag((AppCompatActivity) activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static int getHeightStatusBar(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setWindowFlag(AppCompatActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }

    }

    public static void showSoftKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    public static void openURL(Context mContext, String url) {
        if (!TextUtils.isEmpty(url)) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public static void rateApp(Context mContext) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
    }

    public static DividerItemDecoration simpleDividerItemDecoration(Context context,
                                                                    int orientation,
                                                                    float dpSize,
                                                                    @ColorInt int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, context.getResources().getDisplayMetrics());
        gradientDrawable.setSize(size, size);
        gradientDrawable.setColor(color);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, orientation);
        dividerItemDecoration.setDrawable(gradientDrawable);

        return dividerItemDecoration;
    }

    public static String getVersionName(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    public static boolean isNormalText(String txt) {
        if (TextUtils.isEmpty(txt))
            return false;
        return txt.matches("^[A-Za-z0-9_.]+$");
    }

    public static void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi\nI am using " + context.getString(R.string.app_name) + " app on my android phone\n\nDowload:\n" + "https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share)));
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer);
        }
    }

    public static void feedback(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + Config.EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String out = "Manufaceturer: " + Toolbox.getDeviceName() + "\n" +
                    "OS: " + Build.VERSION.SDK_INT + "\n" +
                    "Version code: " + BuildConfig.VERSION_CODE + "\n" +
                    "Model: " + Build.MODEL;
            intent.putExtra(Intent.EXTRA_TEXT, out);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //TODO smth
        }
    }

    public static void openBrowser(Context mContext, String mLink) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mLink));
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        try {
            Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
                Rect insets = (Rect) mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        } catch (Exception e) {
            // FileLog.e("tmessages", e);
        }
        return 0;
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static String convertToTime(int time) {
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String convertToHourTime(int time) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static boolean isSameDay(long firtTime, long secondTime) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(new Date(firtTime));
        c2.setTime(new Date(secondTime));
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static String getDateString(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM");
        return simpleDateFormat.format(new Date(time));
    }

    public static String convertDuration(String duration) {
        int dur = Integer.valueOf(duration) / 1000;
        int hours = dur / 3600;
        int minutes = (dur % 3600) / 60;
        int seconds = dur % 60;
        if (hours >= 1) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String formatSize(long size) {
        if (size <= 0)
            return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getDurationVideoFile(Context context, File file) {
        String time;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(context, Uri.fromFile(file));
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();
            return time;
        } catch (Exception e) {
            return "0";
        }
    }

    public static String getResolutionVideo(Context context, File file) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(context, Uri.fromFile(file));
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            retriever.release();
            return width + "x" + height;
        } catch (Exception e) {
            return "0";
        }
    }

    public static Bitmap CropBitmapTransparency(Bitmap source) {
        int firstX = 0, firstY = 0;
        int lastX = source.getWidth();
        int lastY = source.getHeight();
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        loop:
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = firstX; x < source.getWidth(); x++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = source.getWidth() - 1; x >= firstX; x--) {
            for (int y = source.getHeight() - 1; y >= firstY; y--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = source.getHeight() - 1; y >= firstY; y--) {
            for (int x = source.getWidth() - 1; x >= firstX; x--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        return Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
    }

    public static String convertTime(String timeIn) {
        DecimalFormat precision = new DecimalFormat("00");
        int time = 0;
        try {
            time = (int) Float.parseFloat(timeIn);
        } catch (Exception e) {
            return "00:00";
        }
        if (time == 0) {
            return "00:00";
        }
        if (time < 60) {
            return "00" + ":" + precision.format(time);
        } else {
            int minuteN = time / 60;
            int secoundN = time % 60;
            if (minuteN < 60) {
                return precision.format(minuteN) + ":" + precision.format(secoundN);
            } else {
                int hourN = minuteN / 60;
                return precision.format(hourN) + ":" + precision.format((minuteN % 60)) + ":" + precision.format(secoundN);
            }
        }
    }

    public static void startActivityAllStage(Context mContext, Intent mIntent) {
        if (mContext instanceof Activity) {
            mContext.startActivity(mIntent);
        } else {
            try {
                PendingIntent.getActivity(mContext, (int) (Math.random() * 9999.0d), mIntent, PendingIntent.FLAG_IMMUTABLE).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                mContext.startActivity(mIntent);
            }
        }
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public static Intent getIntentActionView(Context context, String path) {
        Uri fileUri = FileProvider.getUriForFile(
                context, context.getPackageName() + ".provider",
                new File(path));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK)
                .setDataAndType(
                        fileUri,
                        context.getContentResolver().getType(fileUri));
        return intent;
    }

    public static void scanFile(final Context context, String pathFile) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(pathFile));
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        MediaScannerConnection.scanFile(context, new String[]{pathFile}, null, (path, uri) -> {

        });
    }


}

