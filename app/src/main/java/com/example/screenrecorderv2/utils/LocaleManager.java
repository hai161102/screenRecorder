package com.example.screenrecorderv2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/*
****** In Application
* @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
    }
*
****** In Activity
* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(App.localeManager.setLocale(base));
    }
* */

public class LocaleManager {

    public static final String LANGUAGE_DEFAULT = "en";
    private static LocaleManager localeManager;
    private Context context;

    public static LocaleManager getInstance(Context context) {
        if (localeManager == null) {
            localeManager = new LocaleManager(context);
        }
        return localeManager;
    }

    public LocaleManager(Context context) {
        this.context = context;
    }

    public Context setLocale() {
        return updateResources(PreferencesHelper.getString(PreferencesHelper.KEY_LANGUAGE, LANGUAGE_DEFAULT));
    }

    public Context setNewLocale(String language) {
        PreferencesHelper.putString(PreferencesHelper.KEY_LANGUAGE, language);
        return updateResources(language);
    }

    private Context updateResources(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLayoutDirection(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24(config, locale);
            context = context.createConfigurationContext(config);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setLocaleForApi24(Configuration config, Locale target) {
        Set<Locale> set = new LinkedHashSet<>();
        set.add(target);
        LocaleList all = LocaleList.getDefault();
        for (int i = 0; i < all.size(); i++) {
            set.add(all.get(i));
        }
        Locale[] locales = set.toArray(new Locale[0]);
        config.setLocales(new LocaleList(locales));
    }

    public String[] lstLanguage = new String[]{
            "English",
            "Português", /*Bồ Đào Nha*/
            "Tiếng Việt",
            "русский", /*Nga*/
            "हिन्दी",/*Hindi*/
            "日本語",/*Nhật*/
            "한국어", /*Hàn*/
            "Türk", /*Turkish*/
            "French",
            "Spanish",
            "Ả Rập"
    };

    public String[] lstCodeLanguage = new String[]{
            "en",
            "pt",
            "vi",
            "ru",
            "hi",
            "ja",
            "ko",
            "tr",
            "fr",
            "es",
            "ar"
    };

    public void restart(Activity activity) {
        Intent i = new Intent(activity, activity.getClass());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
    }
}

