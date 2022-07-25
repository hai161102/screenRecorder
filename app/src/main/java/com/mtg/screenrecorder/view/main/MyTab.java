package com.mtg.screenrecorder.view.main;

import androidx.fragment.app.Fragment;

public class MyTab {
    private int mTitle;
    private Fragment mFragment;
    private int mIcon;

    public MyTab(int mTitle, Fragment mFragment, int mIcon) {
        this.mTitle = mTitle;
        this.mFragment = mFragment;
        this.mIcon = mIcon;
    }

    public int getmTitle() {
        return mTitle;
    }

    public void setmTitle(int mTitle) {
        this.mTitle = mTitle;
    }

    public Fragment getmFragment() {
        return mFragment;
    }

    public void setmFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }
}
