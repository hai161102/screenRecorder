package com.mtg.screenrecorder.utils.editvideo;

import com.mtg.screenrecorder.R;

public enum ItemEdit {
    TRIM("Trim", R.drawable.img_trim);
//    COMPRESS("Compress", R.drawable.img_compress),
//    VIDEO_TO_MP3("Video to MP3", R.drawable.img_video_to_mp3),
//    CROP("Crop", R.drawable.img_crop),
//    THEME("Theme", R.drawable.img_theme),
//    GIFGURU("GifGuru", R.drawable.img_gif);
    private String title;
    private int icon;

    ItemEdit(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
