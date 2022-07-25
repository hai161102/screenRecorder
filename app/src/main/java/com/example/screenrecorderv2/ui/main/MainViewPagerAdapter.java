package com.example.screenrecorderv2.ui.main;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.ui.editvideo.EditVideoFragment;
import com.example.screenrecorderv2.ui.picture.PictureFragment;
import com.example.screenrecorderv2.ui.setting.SettingFragment;
import com.example.screenrecorderv2.ui.video.VideoFragment;
import com.example.screenrecorderv2.utils.Config;

import java.util.Arrays;
import java.util.List;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private List<MyTab> tabList;
    private Context context;

    public MainViewPagerAdapter(FragmentManager fm, Context context, MyTab[] tabArr) {
        super(fm);
        this.context = context;
        tabList = Arrays.asList(tabArr);
    }

    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).getmFragment();
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(tabList.get(position).getmTitle());
    }
}
