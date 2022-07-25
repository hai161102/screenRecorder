package com.example.screenrecorderv2.ui.main;

import static com.example.screenrecorderv2.utils.Config.ACTION_SHOW_MAIN_FLOATING;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.ads.control.AdmobHelp;
import com.ads.control.Rate;
import com.example.screenrecorderv2.MyApp;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseActivity;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.base.rx.RxBusType;
import com.example.screenrecorderv2.databinding.ActivityMainBinding;
import com.example.screenrecorderv2.service.MyService;
import com.example.screenrecorderv2.ui.editvideo.EditVideoFragment;
import com.example.screenrecorderv2.ui.guide.GuidePermissionOverlayActivity;
import com.example.screenrecorderv2.ui.picture.PictureFragment;
import com.example.screenrecorderv2.ui.setting.DialogSingleSelected;
import com.example.screenrecorderv2.ui.setting.SettingFragment;
import com.example.screenrecorderv2.ui.video.VideoFragment;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.example.screenrecorderv2.utils.Toolbox;

import java.util.Arrays;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private MainViewPagerAdapter mainViewPagerAdapter;
    private ActionBarDrawerToggle toggle;
    private DialogSingleSelected dialogLanguge;
    private WindowInsets windowInsets;
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 290;
    private DialogAskPermission dialogAskPermission;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(LayoutInflater.from(this));
    }

    public MyTab[] tabArr = {
            new MyTab(R.string.video, new VideoFragment(), R.drawable.ic_tab_video),
            new MyTab(R.string.picture, new PictureFragment(), R.drawable.ic_tab_picture),
            new MyTab(R.string.edit, new EditVideoFragment(), R.drawable.ic_tab_edit),
            new MyTab(R.string.setting, new SettingFragment(), R.drawable.ic_tab_setting),
    };

    @Override
    protected void initView() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        PreferencesHelper.putString(PreferencesHelper.KEY_DEFAULT_RESOLUTION, outSize.x + "x" + outSize.y);
        AdmobHelp.getInstance().loadBanner(this);
        MyApp.getInstance().appOpenManager.showAdIfAvailable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Toolbox.getHeightStatusBar(this) > 0) {
            binding.appBarMain.appbar.setPadding(0, Toolbox.getHeightStatusBar(this), 0, 0);
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitleTextColor(getResources().getColor(android.R.color.transparent));
        binding.navView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    tv.setVisibility(i != position ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.appBarMain.tablayout.setupWithViewPager(binding.appBarMain.contentMain.viewpager);
        for (int i = 0; i < binding.appBarMain.tablayout.getTabCount(); i++) {
            binding.appBarMain.tablayout.getTabAt(i).setIcon(tabArr[i].getmIcon());
            TextView tv = (TextView) (((ViewGroup) ((ViewGroup) binding.appBarMain.tablayout.getChildAt(0)).getChildAt(i)).getChildAt(1));
            tv.setVisibility(i != 0 ? View.GONE : View.VISIBLE);
        }
        checkActionIntent();
        askPermissionOverlay();
        askPermissionStorageMain();
    }

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
            Rate.Show(this, 0);
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