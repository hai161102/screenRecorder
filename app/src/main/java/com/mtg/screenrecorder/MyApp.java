package com.mtg.screenrecorder;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.ads.control.AdmobHelp;
import com.ads.control.AdsApplication;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.view.setting.Apps;
import com.mtg.screenrecorder.view.setting.ItemSelected;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyApp extends AdsApplication {
    public static List<Apps> listTargetApp = null;

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null)
            instance = this;
        AdmobHelp.getInstance().init(this);
        PreferencesHelper.init(this);
        PreferencesHelper.putString(PreferencesHelper.KEY_LANGUAGE,
                this.getResources().getConfiguration().locale.getLanguage());
        initTargetApp();
    }

    @NonNull
    @Override
    public Locale getDefaultLanguage(@NonNull Context context) {
        String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
        for (ItemSelected data : Config.itemsLanguage) {
            if (data.getValue().equals(currentLanguage)) {
                return context.getResources().getConfiguration().locale;
            }
        }
        return Locale.ENGLISH;
    }

    private void initTargetApp() {
        getAllApp()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(datas -> {
                    listTargetApp = datas;
                    RxBusHelper.sendLoadTargetAppFinished();
                }, throwable -> {

                });
    }

    public Single<List<Apps>> getAllApp() {
        return Single.create(sub -> {
            try {
                PackageManager pm = getPackageManager();
                List<Apps> apps = new ArrayList<>();
                // Get list of all installs apps including system apps and apps without any launcher activity
                List<PackageInfo> packages = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : packages) {
                    // Check if the app has launcher intent set and exclude our own app
                    if (!(getPackageName().equals(packageInfo.packageName))
                            && !(pm.getLaunchIntentForPackage(packageInfo.packageName) == null)) {

                        Apps app = new Apps(
                                packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),
                                packageInfo.packageName,
                                packageInfo.applicationInfo.loadIcon(getPackageManager())
                        );
                        // Identify the previously selected app
                        app.setSelectedApp(PreferencesHelper.getString(PreferencesHelper.KEY_APP_SELECTED, "").equals(packageInfo.packageName));
                        apps.add(app);
                    }
                }
                sub.onSuccess(apps);
            } catch (Exception e) {
                sub.onError(e.getCause());
            }
        });
    }
}
