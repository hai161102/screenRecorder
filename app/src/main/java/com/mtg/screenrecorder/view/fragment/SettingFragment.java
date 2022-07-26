package com.mtg.screenrecorder.view.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.BaseFragment;
import com.mtg.screenrecorder.base.rx.RxBusType;
import com.mtg.screenrecorder.databinding.FragmentSettingBinding;
import com.mtg.screenrecorder.service.MyService;
import com.mtg.screenrecorder.view.activity.MainActivity;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.PreferencesHelper;
import com.mtg.screenrecorder.view.dialog.DialogAppPicker;
import com.mtg.screenrecorder.view.dialog.DialogInput;
import com.mtg.screenrecorder.view.dialog.DialogSingleSelected;

import java.util.Arrays;

public class SettingFragment extends BaseFragment<FragmentSettingBinding> {
    private DialogSingleSelected dialogBitRate;
    private DialogSingleSelected dialogFrames;
    private DialogSingleSelected dialogResolution;
    private DialogSingleSelected dialogOrientation;
    private DialogSingleSelected dialogAudio;
    private DialogSingleSelected dialogLanguge;
    private DialogSingleSelected dialogTimer;
    private DialogSingleSelected dialogFileNameFomart;
    private DialogInput dialogFileNamePrefix;
    private DialogAppPicker dialogAppPicker;

    @Override
    protected void initView() {
        binding.layoutVideoSettings.valueResolution.setText(Config.getEntry(Config.itemsResolution, PreferencesHelper.getString(PreferencesHelper.KEY_RESOLUTION, Config.itemsResolution[1].getValue())));
        binding.layoutVideoSettings.valueBitRate.setText(Config.getEntry(Config.itemsBitRate, PreferencesHelper.getString(PreferencesHelper.KEY_BIT_RATE, Config.itemsBitRate[3].getValue())));
        binding.layoutVideoSettings.valueFrames.setText(Config.getEntry(Config.itemsFrame, PreferencesHelper.getString(PreferencesHelper.KEY_FRAMES, Config.itemsFrame[0].getValue())));
        binding.layoutVideoSettings.valueOrientation.setText(Config.getEntry(Config.itemsOrientation, PreferencesHelper.getString(PreferencesHelper.KEY_ORIENTATION, Config.itemsOrientation[0].getValue())));
        binding.layoutAudioSettings.valueAudio.setText(Config.getEntry(Config.itemsAudio, PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.itemsAudio[0].getValue())));
        binding.layoutSaveOptions.valueFileName.setText(Config.getEntry(Config.itemsFileNameFomart, PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue())));
        binding.layoutSaveOptions.valueFileNamePrefix.setText(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording"));
        binding.layoutLanguage.valueLanguage.setText(Config.getEntry(Config.itemsLanguage, PreferencesHelper.getString(PreferencesHelper.KEY_LANGUAGE, Config.itemsLanguage[0].getValue())));
        binding.layoutRecordingSetting.valueCountDownTimer.setText(Config.getEntry(Config.itemsTimer, PreferencesHelper.getString(PreferencesHelper.KEY_TIMER, Config.itemsTimer[1].getValue())));
        binding.layoutRecordingSetting.swFloatingControl.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false));
        binding.layoutRecordingSetting.swVibrate.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_VIBRATE, false));
        binding.layoutCustomApp.swTargetApp.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_TARGET_APP, false));
        binding.layoutExperimental.swSaveAsGif.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_SAVE_AS_GIF, true));
        binding.layoutExperimental.swShake.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_SHAKE, false));
//        checkFloatingControl();
    }

    @Override
    protected void initControl() {
        binding.layoutVideoSettings.containerBitRate.setOnClickListener(v -> {
            dialogBitRate = new DialogSingleSelected(getContext(),
                    getString(R.string.bit_rate),
                    Arrays.asList(Config.itemsBitRate),
                    PreferencesHelper.getString(PreferencesHelper.KEY_BIT_RATE, Config.itemsBitRate[3].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_BIT_RATE, selected);
                        binding.layoutVideoSettings.valueBitRate.setText(Config.getEntry(Config.itemsBitRate, PreferencesHelper.getString(PreferencesHelper.KEY_BIT_RATE, getContext().getResources().getString(Config.itemsBitRate[0].getEntry()))));
                    });
            dialogBitRate.show();
        });

        binding.layoutVideoSettings.containerFrames.setOnClickListener(v -> {
            dialogFrames = new DialogSingleSelected(getContext(),
                    getString(R.string.frame_per_second),
                    Arrays.asList(Config.itemsFrame),
                    PreferencesHelper.getString(PreferencesHelper.KEY_FRAMES, Config.itemsFrame[0].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_FRAMES, selected);
                        binding.layoutVideoSettings.valueFrames.setText(Config.getEntry(Config.itemsFrame, PreferencesHelper.getString(PreferencesHelper.KEY_FRAMES, getContext().getResources().getString(Config.itemsFrame[0].getEntry()))));
                    });
            dialogFrames.show();
        });

        binding.layoutVideoSettings.containerResolution.setOnClickListener(v -> {
            dialogResolution = new DialogSingleSelected(getContext(),
                    getString(R.string.resolution),
                    Arrays.asList(Config.itemsResolution),
                    PreferencesHelper.getString(PreferencesHelper.KEY_RESOLUTION, Config.itemsResolution[1].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_RESOLUTION, selected);
                        binding.layoutVideoSettings.valueResolution.setText(Config.getEntry(Config.itemsResolution, PreferencesHelper.getString(PreferencesHelper.KEY_RESOLUTION, getContext().getResources().getString(Config.itemsResolution[1].getEntry()))));
                    });
            dialogResolution.show();
        });
        binding.layoutVideoSettings.containerOrientation.setOnClickListener(v -> {
            dialogOrientation = new DialogSingleSelected(getContext(),
                    getString(R.string.orientation),
                    Arrays.asList(Config.itemsOrientation),
                    PreferencesHelper.getString(PreferencesHelper.KEY_ORIENTATION, Config.itemsOrientation[0].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_ORIENTATION, selected);
                        binding.layoutVideoSettings.valueOrientation.setText(Config.getEntry(Config.itemsOrientation, PreferencesHelper.getString(PreferencesHelper.KEY_ORIENTATION, getContext().getResources().getString(Config.itemsOrientation[0].getEntry()))));
                    });
            dialogOrientation.show();
        });
        binding.layoutAudioSettings.containerRecordAudio.setOnClickListener(v -> {
            dialogAudio = new DialogSingleSelected(getContext(),
                    getString(R.string.record_audio),
                    Arrays.asList(Config.itemsAudio),
                    PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.itemsAudio[0].getValue()),
                    selected -> {
                        if (selected.equals(Config.itemsAudio[1].getValue()) || selected.equals(Config.itemsAudio[2].getValue()) || selected.equals(Config.itemsAudio[3].getValue())) {
                            getBaseActivity().askPermissionRecord(() -> {
                                PreferencesHelper.putString(PreferencesHelper.KEY_RECORD_AUDIO, selected);
                                binding.layoutAudioSettings.valueAudio.setText(Config.getEntry(Config.itemsAudio, PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, getContext().getResources().getString(Config.itemsAudio[0].getEntry()))));
                                return null;
                            });
                        } else {
                            PreferencesHelper.putString(PreferencesHelper.KEY_RECORD_AUDIO, selected);
                            binding.layoutAudioSettings.valueAudio.setText(Config.getEntry(Config.itemsAudio, PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, getContext().getResources().getString(Config.itemsAudio[0].getEntry()))));
                        }
                    });
            dialogAudio.show();
        });
        binding.layoutSaveOptions.containerFileName.setOnClickListener(v -> {
            dialogFileNameFomart = new DialogSingleSelected(getContext(),
                    getString(R.string.file_name_fomart),
                    Arrays.asList(Config.itemsFileNameFomart),
                    PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_FILE_NAME_FOMART, selected);
                        binding.layoutSaveOptions.valueFileName.setText(Config.getEntry(Config.itemsFileNameFomart, PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, getContext().getResources().getString(Config.itemsFileNameFomart[0].getEntry()))));
                    });
            dialogFileNameFomart.show();
        });
        binding.layoutSaveOptions.containerFileNamePrefix.setOnClickListener(v -> {
            dialogFileNamePrefix = new DialogInput(getContext(),
                    getString(R.string.file_name_prefix),
                    PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording"),
                    input -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_FILE_NAME_PREFIX, input);
                        binding.layoutSaveOptions.valueFileNamePrefix.setText(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording"));
                    });
            dialogFileNamePrefix.show();
        });
        binding.layoutLanguage.containerLanguage.setOnClickListener(v -> {
            dialogLanguge = new DialogSingleSelected(getContext(),
                    getString(R.string.menu_language),
                    Arrays.asList(Config.itemsLanguage),
                    PreferencesHelper.getString(PreferencesHelper.KEY_LANGUAGE, Config.itemsLanguage[0].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_LANGUAGE, selected);
                        getBaseActivity().setLanguage(selected);
                    });
            dialogLanguge.show();
        });
        binding.layoutRecordingSetting.containerCountDownTimer.setOnClickListener(v -> {
            dialogTimer = new DialogSingleSelected(getContext(),
                    getString(R.string.count_down_timer),
                    Arrays.asList(Config.itemsTimer),
                    PreferencesHelper.getString(PreferencesHelper.KEY_TIMER, Config.itemsTimer[1].getValue()),
                    selected -> {
                        PreferencesHelper.putString(PreferencesHelper.KEY_TIMER, selected);
                        binding.layoutRecordingSetting.valueCountDownTimer.setText(Config.getEntry(Config.itemsTimer, PreferencesHelper.getString(PreferencesHelper.KEY_TIMER, getContext().getResources().getString(Config.itemsTimer[0].getEntry()))));
                    });
            dialogTimer.show();
        });
        binding.layoutCustomApp.containerTargetApp.setOnClickListener(v -> {
            binding.layoutCustomApp.swTargetApp.setChecked(!binding.layoutCustomApp.swTargetApp.isChecked());
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_TARGET_APP, binding.layoutCustomApp.swTargetApp.isChecked());
        });
        binding.layoutCustomApp.containerChosseApp.setOnClickListener(v -> {
            dialogAppPicker = new DialogAppPicker(getContext());
            dialogAppPicker.show();
        });
        binding.layoutRecordingSetting.containerFloatingControl.setOnClickListener(v -> {
//            PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, binding.layoutRecordingSetting.swFloatingControl.isChecked());
            boolean currentState = PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false);
            checkFloatingControl(currentState);
        });
        binding.layoutRecordingSetting.containerVibrate.setOnClickListener(v -> {
            binding.layoutRecordingSetting.swVibrate.setChecked(!binding.layoutRecordingSetting.swVibrate.isChecked());
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_VIBRATE, binding.layoutRecordingSetting.swVibrate.isChecked());
        });
        binding.layoutExperimental.containerSaveAsGif.setOnClickListener(v -> {
            binding.layoutExperimental.swSaveAsGif.setChecked(!binding.layoutExperimental.swSaveAsGif.isChecked());
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_SAVE_AS_GIF, binding.layoutExperimental.swSaveAsGif.isChecked());
        });
        binding.layoutExperimental.containerShake.setOnClickListener(v -> {
            binding.layoutExperimental.swShake.setChecked(!binding.layoutExperimental.swShake.isChecked());
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_SHAKE, binding.layoutExperimental.swShake.isChecked());
        });
    }

    private void checkFloatingControl(boolean currentState) {
        if (!currentState) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).askPermissionOverlay();
            }
        } else {
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false);
            binding.layoutRecordingSetting.swFloatingControl.setChecked(false);
            Intent intent = new Intent(getActivity(), MyService.class);
            intent.setAction(Config.ACTION_DISABLE_FLOATING);
            getActivity().startService(intent);
        }
    }

    @Override
    protected boolean isNeedRefresh() {
        return false;
    }

    @Override
    protected FragmentSettingBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSettingBinding.inflate(LayoutInflater.from(getContext()));
    }

    @Override
    public void onReceivedEvent(RxBusType type, Object data) {
        switch (type) {
            case PERMISSION_GRANTED:
                binding.layoutRecordingSetting.swFloatingControl.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogBitRate != null) {
            dialogBitRate.dismiss();
        }
        if (dialogFrames != null) {
            dialogFrames.dismiss();
        }
        if (dialogResolution != null) {
            dialogResolution.dismiss();
        }
        if (dialogOrientation != null) {
            dialogOrientation.dismiss();
        }
        if (dialogAudio != null) {
            dialogAudio.dismiss();
        }
        if (dialogFileNameFomart != null) {
            dialogFileNameFomart.dismiss();
        }
        if (dialogFileNamePrefix != null) {
            dialogFileNamePrefix.dismiss();
        }
        if (dialogLanguge != null) {
            dialogLanguge.dismiss();
        }
        if (dialogTimer != null) {
            dialogTimer.dismiss();
        }
        if (dialogAppPicker != null) {
            dialogAppPicker.dismiss();
        }
    }
}
