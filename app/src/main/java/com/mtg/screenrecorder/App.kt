package com.mtg.screenrecorder

import android.content.Context
import com.common.control.MyApplication
import com.mtg.screenrecorder.base.rx.RxBusHelper
import com.mtg.screenrecorder.utils.Config
import com.mtg.screenrecorder.utils.PreferencesHelper
import com.mtg.screenrecorder.view.setting.Apps
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "UPPER_BOUND_VIOLATED_BASED_ON_JAVA_ANNOTATIONS"
)
class App : MyApplication() {

    override fun onApplicationCreate() {
        PreferencesHelper.init(this)
        PreferencesHelper.putString(
            PreferencesHelper.KEY_LANGUAGE,
            this.resources.configuration.locale.language
        )
        initTargetApp()
    }

    fun getDefaultLanguage(context: Context): Locale {
        val currentLanguage = context.resources.configuration.locale.language
        for (data in Config.itemsLanguage) {
            if (data.value == currentLanguage) {
                return context.resources.configuration.locale
            }
        }
        return Locale.ENGLISH
    }
    override fun hasAds(): Boolean {
        return false
    }

    override fun isShowDialogLoadingAd(): Boolean {
        return false
    }

    override fun isShowAdsTest(): Boolean {
        return false
    }

    override fun enableAdsResume(): Boolean {
        return false
    }

    override fun getOpenAppAdId(): String {
        return ""
    }
    private fun initTargetApp() {
        getAllApp()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ datas: List<Apps?>? ->
                listTargetApp = datas as List<Apps>
                RxBusHelper.sendLoadTargetAppFinished()
            }) { throwable: Throwable? -> }
    }

    private fun getAllApp(): Single<List<Apps?>?> {
        return Single.create { sub: SingleEmitter<List<Apps?>?> ->
            try {
                val pm = packageManager
                val apps: MutableList<Apps?> = ArrayList()
                // Get list of all installs apps including system apps and apps without any launcher activity
                val packages =
                    pm.getInstalledPackages(0)
                for (packageInfo in packages) {
                    // Check if the app has launcher intent set and exclude our own app
                    if (packageName != packageInfo.packageName
                        && pm.getLaunchIntentForPackage(packageInfo.packageName) != null
                    ) {
                        val app = Apps(
                            packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                            packageInfo.packageName,
                            packageInfo.applicationInfo.loadIcon(packageManager)
                        )
                        // Identify the previously selected app
                        app.setSelectedApp(
                            PreferencesHelper.getString(
                                PreferencesHelper.KEY_APP_SELECTED,
                                ""
                            ) == packageInfo.packageName
                        )
                        apps.add(app)
                    }
                }
                sub.onSuccess(apps)
            } catch (e: Exception) {
                sub.onError(e.cause)
            }
        }
    }
    companion object{
        lateinit var listTargetApp : List<Apps>
    }

}