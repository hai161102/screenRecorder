package com.mtg.screenrecorder.utils;

import androidx.fragment.app.Fragment;

public class MyTabChange extends MyTab{
    private int iconChange;
    private boolean isChange;
    public MyTabChange(int mTitle, Fragment mFragment, int mIcon, int iconChange, boolean isChange) {
        super(mTitle, mFragment, mIcon);
        this.isChange = isChange;
        this.iconChange = iconChange;
        if (isChange){
            this.setmIcon(this.iconChange);
        }
    }

    public int getIconChange() {
        return iconChange;
    }

    public void setIconChange(int iconChange) {
        this.iconChange = iconChange;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
        if (isChange){
            this.setmIcon(this.iconChange);
        }
    }
}
