package com.mtg.screenrecorder.view.setting;

public class ItemSelected {
    private int entry;
    private String value;
    private int description = 0;

    public ItemSelected(int entry, String value) {
        this.entry = entry;
        this.value = value;
    }

    public ItemSelected(int entry, String value, int description) {
        this.entry = entry;
        this.value = value;
        this.description = description;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }
}
