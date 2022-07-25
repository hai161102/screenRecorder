package com.mtg.screenrecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static SharedPreferences sharedPreferences;
    private static final String NAME = "MyPref";

    public static final String FIRT_INSTALL = "firt install";

    public static final String KEY_DEFAULT_RESOLUTION = "default resolution";
    public static final String KEY_BIT_RATE = "bit rate";
    public static final String KEY_RESOLUTION = "resolution";
    public static final String KEY_FRAMES = "frames";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_RECORD_AUDIO = "audio";
    public static final String KEY_TARGET_APP = "target app";
    public static final String KEY_SAVE_LOCATION = "save location";
    public static final String KEY_FILE_NAME_FOMART = "file name";
    public static final String KEY_FILE_NAME_PREFIX = "file name prefix";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_TIMER = "timer";
    public static final String KEY_FLOATING_CONTROL = "floating control";
    public static final String KEY_VIBRATE = "vibrate";
    public static final String KEY_APP_SELECTED = "app selected";
    public static final String KEY_SAVE_AS_GIF = "save as gif";
    public static final String KEY_SHAKE = "shake";

    public static final String PREFS_TOOLS_SCREEN_SHOT = "tools_screenshot";
    public static final String PREFS_TOOLS_CAMERA = "tools_camera";
    public static final String PREFS_TOOLS_BRUSH = "tools_brush";

    public static void init(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor editor() {
        return sharedPreferences.edit();
    }

    public static void putBoolean(String key, boolean value) {
        editor().putBoolean(key, value).apply();
    }

    public static void putString(String key, String value) {
        editor().putString(key, value).apply();
    }

    public static void putInt(String key, int value) {
        editor().putInt(key, value).apply();
    }

    public static void putLong(String key, long value) {
        editor().putLong(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultvalue) {
        return sharedPreferences.getBoolean(key, defaultvalue);
    }

    public static String getString(String key,String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
}
