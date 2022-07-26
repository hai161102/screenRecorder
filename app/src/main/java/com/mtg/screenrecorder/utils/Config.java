package com.mtg.screenrecorder.utils;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.utils.setting.ItemSelected;

public class Config {

    public static final String ACTION_SHOW_TOOLS = "action.show_tools";
    public static final String ACTION_SHOW_CAMERA = "action.show_camera";
    public static final String ACTION_SCREEN_SHOT_START = "action.screens_shot";
    public static final String ACTION_SCREEN_RECORDING_START = "action.start_recording";
    public static final String ACTION_SHOW_MAIN_FLOATING = "action.show_main_floating";
    public static final String ACTION_OPEN_SETTING = "action.open_setting";
    public static final String ACTION_OPEN_MAIN = "action.open_main";
    public static final String ACTION_DISABLE_FLOATING = "action.disable_floating";
    public static final String ACTION_STOP_SHAKE = "action.shake";


    public static final String KEY_SCREEN_SHOT_RESULT_CODE = "screen shot result";
    public static final String KEY_SCREEN_SHOT_INTENT = "screen shot intent";
    public static final String KEY_SCREEN_RECORD_INTENT = "screen record intent";
    public static final String KEY_SCREEN_RECORD_RESULT_CODE = "screen record result";

    public static final String EXTRA_PATH = "extra path";
    public static final String EMAIL = "abc@gmail.com";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int getEntry(ItemSelected[] itemSelecteds, String value) {
        for (ItemSelected itemSelected : itemSelecteds) {
            if (itemSelected.getValue().equals(value)) {
                return itemSelected.getEntry();
            }
        }
        return itemSelecteds[0].getEntry();
    }

    public static final ItemSelected[] itemsBitRate = {
            new ItemSelected(R.string.bitrate_0, "1048576"),
            new ItemSelected(R.string.bitrate_1, "2621440"),
            new ItemSelected(R.string.bitrate_2, "3670016"),
            new ItemSelected(R.string.bitrate_3, "4718592"),
            new ItemSelected(R.string.bitrate_4, "7130317"),
            new ItemSelected(R.string.bitrate_5, "10276045"),
            new ItemSelected(R.string.bitrate_6, "12582912"),
            new ItemSelected(R.string.bitrate_7, "25165824"),
            new ItemSelected(R.string.bitrate_8, "50331648"),
    };

    public static final ItemSelected[] itemsResolution = {
            new ItemSelected(R.string.resolution_0, "360"),
            new ItemSelected(R.string.resolution_1, "720"),
            new ItemSelected(R.string.resolution_2, "1080"),
            new ItemSelected(R.string.resolution_3, "1440"),
    };

    public static final ItemSelected[] itemsFrame = {
            new ItemSelected(R.string.frame_0, "25"),
            new ItemSelected(R.string.frame_1, "30"),
            new ItemSelected(R.string.frame_2, "35"),
            new ItemSelected(R.string.frame_3, "40"),
            new ItemSelected(R.string.frame_4, "50"),
            new ItemSelected(R.string.frame_5, "60"),
    };

    public static final String ORIENTATION_AUTO = "auto";
    public static final String ORIENTATION_PORTRAIT = "portrait";
    public static final String ORIENTATION_LANDSCAPE = "landscape";

    public static final ItemSelected[] itemsOrientation = {
            new ItemSelected(R.string.orientation_0, ORIENTATION_AUTO),
            new ItemSelected(R.string.orientation_1, ORIENTATION_PORTRAIT),
            new ItemSelected(R.string.orientation_2, ORIENTATION_LANDSCAPE),
    };

    public static final String AUDIO_NONE = "none";
    public static final String AUDIO_MIC = "mic";
    public static final String AUDIO_INTERNAL = "internal";
    public static final String AUDIO_MIC_AND_INTERNAL = "mic and internal";

    public static final ItemSelected[] itemsAudio = {
            new ItemSelected(R.string.audio_title_0, AUDIO_NONE, R.string.audio_description_0),
            new ItemSelected(R.string.audio_title_1, AUDIO_MIC, R.string.audio_description_1),
            new ItemSelected(R.string.audio_title_2, AUDIO_INTERNAL, R.string.audio_description_2),
            new ItemSelected(R.string.audio_title_3, AUDIO_MIC_AND_INTERNAL, R.string.audio_description_3),
    };

    public static final ItemSelected[] itemsFileNameFomart = {
            new ItemSelected(R.string.name_format_0, "yyyyMMdd_hhmmss"),
            new ItemSelected(R.string.name_format_1, "ddMMyyyy_hhmmss"),
            new ItemSelected(R.string.name_format_2, "yyMMdd_hhmmss"),
            new ItemSelected(R.string.name_format_3, "ddMMyy_hhmmss"),
            new ItemSelected(R.string.name_format_4, "hhMMss_yyyymmdd"),
            new ItemSelected(R.string.name_format_5, "hhMMss_ddmmyyyy"),
            new ItemSelected(R.string.name_format_6, "hhMMss_yymmdd"),
            new ItemSelected(R.string.name_format_7, "hhMMss_ddmmyy"),
    };

    public static final ItemSelected[] itemsLanguage = {
            new ItemSelected(R.string.language_0, "en"),
            new ItemSelected(R.string.language_1, "pt"),
            new ItemSelected(R.string.language_2, "vi"),
            new ItemSelected(R.string.language_3, "ru"),
            new ItemSelected(R.string.language_4, "hi"),
            new ItemSelected(R.string.language_5, "ja"),
            new ItemSelected(R.string.language_6, "ko"),
            new ItemSelected(R.string.language_7, "tr"),
            new ItemSelected(R.string.language_8, "fr"),
            new ItemSelected(R.string.language_9, "es"),
            new ItemSelected(R.string.language_10, "ar"),
    };

    public static final ItemSelected[] itemsTimer = {
            new ItemSelected(R.string.timer_0, "0"),
            new ItemSelected(R.string.timer_1, "3"),
            new ItemSelected(R.string.timer_2, "5"),
            new ItemSelected(R.string.timer_3, "7"),
            new ItemSelected(R.string.timer_4, "10")
    };
}
