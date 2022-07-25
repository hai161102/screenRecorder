package com.mtg.screenrecorder.view.main;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
