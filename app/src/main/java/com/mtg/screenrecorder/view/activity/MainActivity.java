package com.mtg.screenrecorder.view.activity;

import static com.mtg.screenrecorder.utils.Config.ACTION_SHOW_MAIN_FLOATING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.MyTab;
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
    private WindowInsets windowInsets;
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 290;
    private DialogAskPermission dialogAskPermission;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(LayoutInflater.from(this));
    }

    public MyTab[] tabArr = {
            new MyTab(R.string.video, new VideoFragment(), R.drawable.ic_tab_video),
            new MyTab(R.string.picture, new PictureFragment(), R.drawable.ic_image),
            new MyTab(R.string.edit, new EditVideoFragment(), R.drawable.ic_edit),
            new MyTab(R.string.setting, new SettingFragment(), R.drawable.ic_setting),
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

        binding.appBarMain.tablayout.setupWithViewPager(binding.appBarMain.contentMain.viewpager);
        for (int i = 0; i < binding.appBarMain.tablayout.getTabCount(); i++) {
            binding.appBarMain.tablayout.getTabAt(i).setIcon(tabArr[i].getmIcon());
            TextView tv = (TextView) (((ViewGroup) ((ViewGroup) binding.appBarMain.tablayout.getChildAt(0)).getChildAt(i)).getChildAt(1));
        }
        checkActionIntent();
        askPermissionOverlay();
        askPermissionStorageMain();
        binding.appBarMain.tablayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.appBarMain.contentMain.viewpager){
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                super.onTabSelected(tab);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabColor = ContextCompat.getColor(MainActivity.this, R.color.color_94979D);
                tab.getIcon().setColorFilter(tabColor, PorterDuff.Mode.ADD);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                int tabColor = ContextCompat.getColor(MainActivity.this, R.color.color_FB8500);
                tab.getIcon().setColorFilter(tabColor, PorterDuff.Mode.ADD);
            }
        });
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