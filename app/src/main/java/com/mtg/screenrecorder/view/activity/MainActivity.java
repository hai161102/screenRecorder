package com.mtg.screenrecorder.view.activity;

import static com.mtg.screenrecorder.utils.Config.ACTION_SHOW_MAIN_FLOATING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.BaseActivity;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.base.rx.RxBusType;
import com.mtg.screenrecorder.databinding.ActivityMainBinding;
import com.mtg.screenrecorder.service.MyService;
import com.mtg.screenrecorder.service.floating.FloatingBrushManager;
import com.mtg.screenrecorder.service.floating.FloatingCameraViewManager;
import com.mtg.screenrecorder.service.floating.FloatingMainManager;
import com.mtg.screenrecorder.service.floating.FloatingScreenShotManager;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.MyTab;
import com.mtg.screenrecorder.utils.MyTabChange;
import com.mtg.screenrecorder.utils.PreferencesHelper;
import com.mtg.screenrecorder.utils.Toolbox;
import com.mtg.screenrecorder.view.activity.guide.GuidePermissionOverlayActivity;
import com.mtg.screenrecorder.view.adapter.MainViewPagerAdapter;
import com.mtg.screenrecorder.view.dialog.DialogAskPermission;
import com.mtg.screenrecorder.view.dialog.DialogSingleSelected;
import com.mtg.screenrecorder.view.fragment.EditVideoFragment;
import com.mtg.screenrecorder.view.fragment.PictureFragment;
import com.mtg.screenrecorder.view.fragment.SettingFragment;
import com.mtg.screenrecorder.view.fragment.VideoFragment;

import java.util.Arrays;
import java.util.Objects;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private MainViewPagerAdapter mainViewPagerAdapter;
    private ActionBarDrawerToggle toggle;
    private DialogSingleSelected dialogLanguge;
    public static WindowInsets windowInsets;
    private FloatingCameraViewManager floatingCameraViewManager;
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 290;
    private DialogAskPermission dialogAskPermission;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(LayoutInflater.from(this));
    }

    public MyTab[] tabArr = {
            new MyTab(R.string.video, new VideoFragment(), R.drawable.ic_video_non),
            new MyTab(R.string.picture, new PictureFragment(), R.drawable.ic_image),
            new MyTab(R.string.edit, new EditVideoFragment(), R.drawable.ic_edit),
            new MyTab(R.string.setting, new SettingFragment(), R.drawable.ic_setting),
    };
    public MyTabChange[] tabChanges = {
            new MyTabChange(R.string.video, new VideoFragment(), R.drawable.ic_video_non, R.drawable.ic_video, false),
            new MyTabChange(R.string.picture, new PictureFragment(), R.drawable.ic_image, R.drawable.ic_image_unnon, false),
            new MyTabChange(R.string.edit, new EditVideoFragment(), R.drawable.ic_edit, R.drawable.ic_edit_unnon, false),
            new MyTabChange(R.string.setting, new SettingFragment(), R.drawable.ic_setting, R.drawable.ic_setting_unnon, false),
    };

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void initView() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        PreferencesHelper.putString(PreferencesHelper.KEY_DEFAULT_RESOLUTION, outSize.x + "x" + outSize.y);
//        AdmobHelp.getInstance().loadBanner(this);
//        MyApp.getInstance().appOpenManager.showAdIfAvailable();
        initViewMain();

        checkActionIntent();
        askPermissionOverlay();
        askPermissionStorageMain();
        setViewTools();
        setActionEvent();
        FloatingMainManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);
        FloatingScreenShotManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);
        FloatingBrushManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);

    }


    public void setViewTools() {
        boolean isFloatMain = PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false);
        boolean isCameraMain = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false);
        boolean isScreenShotMain = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false);
        boolean isBrush = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false);
        binding.appBarMain.floatingMain.setImageResource(isFloatMain ? R.drawable.ic_floating : R.drawable.ic_floating_ball_non);
        binding.appBarMain.tvFloatingMain.setTextColor(isFloatMain ? ContextCompat.getColor(this, R.color.color_3c3c3c) : ContextCompat.getColor(this, R.color.color_94979D));
        binding.appBarMain.cameraMain.setImageResource(isCameraMain ? R.drawable.ic_camera_unnon : R.drawable.ic_camera);
        binding.appBarMain.tvCameraMain.setTextColor(isCameraMain ? ContextCompat.getColor(this, R.color.color_3c3c3c) : ContextCompat.getColor(this, R.color.color_94979D));
        binding.appBarMain.screenshotMain.setImageResource(isScreenShotMain ? R.drawable.ic_screen_shot_unnnon : R.drawable.ic_screenshot);
        binding.appBarMain.tvScreenShotMain.setTextColor(isScreenShotMain ? ContextCompat.getColor(this, R.color.color_3c3c3c) : ContextCompat.getColor(this, R.color.color_94979D));
        binding.appBarMain.brushMain.setImageResource(isBrush ? R.drawable.ic_brush_unnon : R.drawable.ic_brush);
        binding.appBarMain.tvBrushMain.setTextColor(isBrush ? ContextCompat.getColor(this, R.color.color_3c3c3c) : ContextCompat.getColor(this, R.color.color_94979D));
    }

    private void initViewMain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Toolbox.getHeightStatusBar(this) > 0) {
            binding.appBarMain.appbar.setPadding(0, Toolbox.getHeightStatusBar(this), 0, 0);
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitleTextColor(getResources().getColor(android.R.color.transparent));
        binding.navView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setNavigationIcon(R.drawable.ic_home);
        setTitleToolbar(getString(R.string.screen_recorder));
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this, tabArr);
        binding.appBarMain.contentMain.viewpager.setAdapter(mainViewPagerAdapter);
        binding.appBarMain.contentMain.viewpager.setOffscreenPageLimit(5);
        binding.appBarMain.contentMain.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < binding.appBarMain.tablayout.getTabCount(); i++) {
                    TextView tv = (TextView) (((ViewGroup) ((ViewGroup) binding.appBarMain.tablayout.getChildAt(0)).getChildAt(i)).getChildAt(1));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        ColorStateList colorStateList = new ColorStateList()
//        binding.appBarMain.tablayout.setTabIconTint();
        binding.appBarMain.tablayout.setupWithViewPager(binding.appBarMain.contentMain.viewpager);
        for (int i = 0; i < binding.appBarMain.tablayout.getTabCount(); i++) {
            binding.appBarMain.tablayout.getTabAt(i).setIcon(tabArr[i].getmIcon());
            if (i == 0) {
                binding.appBarMain.tablayout.getTabAt(i).getIcon().setTint(Color.parseColor("#FB8500"));
            }
            TextView tv = (TextView) (((ViewGroup) ((ViewGroup) binding.appBarMain.tablayout.getChildAt(0)).getChildAt(i)).getChildAt(1));
        }
        binding.appBarMain.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(Color.parseColor("#FB8500"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(Color.parseColor("#94979D"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(Color.parseColor("#FB8500"));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewTools();
    }

    private void setActionEvent() {
        binding.appBarMain.viewFloating.setOnClickListener(v -> {
            boolean isFloating = PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false);
            if (!isFloating) {
                PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
                startService();

            } else {
                PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false);
                putAction(Config.ACTION_DISABLE_FLOATING);
            }
            binding.appBarMain.floatingMain.setImageResource(isFloating ? R.drawable.ic_floating_ball_non : R.drawable.ic_floating);
            binding.appBarMain.tvFloatingMain.setTextColor(
                    isFloating ? ContextCompat.getColor(this, R.color.color_94979D)
                            : ContextCompat.getColor(this, R.color.color_3c3c3c)
            );
            FloatingMainManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);

        });
        binding.appBarMain.viewCamera.setOnClickListener(v -> {
            boolean isCamera = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false);
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, !isCamera);
            if (!isCamera){
                floatingCameraViewManager = new FloatingCameraViewManager(this, this);
            }else {
                floatingCameraViewManager.onFinishFloatingView();
            }
//            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_CAMERA, !isCamera);
            binding.appBarMain.cameraMain.setImageResource(isCamera ? R.drawable.ic_camera : R.drawable.ic_camera_unnon);
            binding.appBarMain.tvCameraMain.setTextColor(
                    isCamera ? ContextCompat.getColor(this, R.color.color_94979D)
                            : ContextCompat.getColor(this, R.color.color_3c3c3c)
            );
        });
        binding.appBarMain.viewScreenShot.setOnClickListener(v -> {
            boolean isScreenShot = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false);
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, !isScreenShot);
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_SCREEN_SHOT, !isScreenShot);
//            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_SCREEN_SHOT, !isScreenShot);
            binding.appBarMain.screenshotMain.setImageResource(isScreenShot ? R.drawable.ic_screenshot : R.drawable.ic_screen_shot_unnnon);
            binding.appBarMain.tvScreenShotMain.setTextColor(
                    isScreenShot ? ContextCompat.getColor(this, R.color.color_94979D)
                            : ContextCompat.getColor(this, R.color.color_3c3c3c)
            );
            FloatingScreenShotManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);

        });
        binding.appBarMain.viewBrush.setOnClickListener(v -> {
            boolean isBrush = PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false);
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, !isBrush);
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_BRUSH, !isBrush);
            binding.appBarMain.brushMain.setImageResource(isBrush ? R.drawable.ic_brush : R.drawable.ic_brush_unnon);
            binding.appBarMain.tvBrushMain.setTextColor(
                    isBrush ? ContextCompat.getColor(this, R.color.color_94979D)
                            : ContextCompat.getColor(this, R.color.color_3c3c3c)
            );
            FloatingBrushManager.getInstance(this, FloatingViewManager.findCutoutSafeArea(windowInsets)).setMainActivity(this);

        });
    }

    private void putAction(String action) {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(action);
        startService(intent);
    }

//    private void setIcon(TabLayout.Tab tab, int currentItem) {
//        switch (currentItem){
//            case 0:
//                if (tab.isSelected()){
//                    tab.setIcon(R.drawable.ic_video);
//                }
//                else {
//                    tab.setIcon(R.drawable.ic_video_non);
//                }
//                break;
//            case 1:
//                if (tab.isSelected()){
//                    tab.setIcon(R.drawable.ic_image_unnon);
//                }
//                else {
//                    tab.setIcon(R.drawable.ic_image);
//                }
//                break;
//            case 2:
//                if (tab.isSelected()){
//                    tab.setIcon(R.drawable.ic_edit_unnon);
//                }
//                else {
//                    tab.setIcon(R.drawable.ic_edit);
//                }
//                break;
//            case 3:
//                if (tab.isSelected()){
//                    tab.setIcon(R.drawable.ic_setting_unnon);
//                }
//                else {
//                    tab.setIcon(R.drawable.ic_setting);
//                }
//                break;
//        }
//    }

    private void checkActionIntent() {
        if (getIntent() != null && getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case Config.ACTION_OPEN_SETTING:
                    binding.appBarMain.contentMain.viewpager.setCurrentItem(3);
                    break;
                case Config.ACTION_OPEN_MAIN:
                    binding.appBarMain.contentMain.viewpager.setCurrentItem(0);
                    break;

            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            windowInsets = getWindow().getDecorView().getRootWindowInsets();
        }
        startService();
    }

    private void startService() {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(ACTION_SHOW_MAIN_FLOATING);
        intent.putExtra(ACTION_SHOW_MAIN_FLOATING, FloatingViewManager.findCutoutSafeArea(windowInsets));
        startService(intent);
    }

    private void onPermissionGranted() {
        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
        RxBusHelper.sendPermissionOverlayGranted();
    }

    public void askPermissionStorageMain() {
        askPermissionStorage(() -> {
            RxBusHelper.sendNotiMediaChange();
            return null;
        });
    }

    public static boolean isSystemAlertPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return Settings.canDrawOverlays(context);
    }

    public void askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            onPermissionGranted();
        } else {
            dialogAskPermission = DialogAskPermission.getInstance(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, () -> {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName())), REQUEST_SETTING_OVERLAY_PERMISSION);
                startActivity(new Intent(this, GuidePermissionOverlayActivity.class));
            });
            dialogAskPermission.show(getSupportFragmentManager(), DialogAskPermission.class.getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_OVERLAY_PERMISSION) {
            new Handler().postDelayed(() -> {
                if (isSystemAlertPermissionGranted(MainActivity.this)) {
                    onPermissionGranted();
                }
            }, 200);
        }
    }

    @Override
    protected void initControl() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_feedback:
                    Toolbox.feedback(this);
                    break;
                case R.id.nav_rate:
                    Toolbox.rateApp(this);
                    break;
                case R.id.nav_share:
                    Toolbox.shareApp(this);
                    break;
                case R.id.nav_language:
                    dialogLanguge = new DialogSingleSelected(this,
                            getString(R.string.menu_language),
                            Arrays.asList(Config.itemsLanguage),
                            getCurrentLanguage().getLanguage(),
                            selected -> setLanguage(selected));
                    dialogLanguge.show();
                    break;
            }
            binding.drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.appBarMain.contentMain.viewpager.getCurrentItem() == 0) {
//            Rate.Show(this, 0);
        } else {
            binding.appBarMain.contentMain.viewpager.setCurrentItem(0, true);
        }
    }

    public void setStageDrawerLayout(boolean isLock) {
        binding.drawerLayout.setDrawerLockMode(isLock ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onReceivedEvent(RxBusType type, Object data) {
        switch (type) {
            case PERMISSION_GRANTED:
                if (windowInsets != null) {
                    startService();
                }
                break;
        }
    }

    @Override
    protected TextView getToolbarTitle() {
        return binding.appBarMain.toolbarTitle;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.appBarMain.toolbar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLanguge != null) {
            dialogLanguge.dismiss();
        }
        if (dialogAskPermission != null) {
            dialogAskPermission.dismiss();
        }
    }
}