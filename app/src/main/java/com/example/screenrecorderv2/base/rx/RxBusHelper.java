package com.example.screenrecorderv2.base.rx;

import com.example.screenrecorderv2.utils.ScreenRecordHelper;

public class RxBusHelper {
    public static void sendTrimVideoSuccess() {
        RxBus.getInstance().send(new BusEvent(RxBusType.TRIM_VIDEO, null));
    }

    public static void sendPermissionOverlayGranted() {
        RxBus.getInstance().send(new BusEvent(RxBusType.PERMISSION_GRANTED, null));
    }

    public static void sendNotiMediaChange() {
        RxBus.getInstance().send(new BusEvent(RxBusType.NOTI_MEDIA_CHANGE, null));
    }

    public static void sendScreenRecordSuccess(String dstPath) {
        RxBus.getInstance().send(new BusEvent(RxBusType.SCREEN_RECORD_SUCCESS, dstPath));
    }

    public static void sendStartScreenShot() {
        RxBus.getInstance().send(new BusEvent(RxBusType.START_SCREEN_SHOT, null));
    }

    public static void sendScreenShot(String dstPath) {
        RxBus.getInstance().send(new BusEvent(RxBusType.SCREEN_SHOT, dstPath));
    }

    public static void sendCheckedTools(RxBusType type, boolean isChecked) {
        RxBus.getInstance().send(new BusEvent(type, isChecked));
    }

    public static void sendClickBrushScreenShot() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_SCREEN_SHOT_BRUSH, null));
    }

    public static void sendClickNotificationScreenShot() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_SCREEN_SHOT, null));
    }

    public static void sendClickNotificationScreenRecord() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_SCREEN_RECORD, null));
    }

    public static void sendClickNotificationTools() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_TOOLS, null));
    }

    public static void sendClickNotificationHome() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_HOME, null));
    }

    public static void sendClickNotificationExit() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_EXIT, null));
    }

    public static void sendClickNotificationPause() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_PAUSE, null));
    }

    public static void sendClickNotificationResume() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_RESUME, null));
    }

    public static void sendClickNotificationStop() {
        RxBus.getInstance().send(new BusEvent(RxBusType.CLICK_NOTIFICATION_STOP, null));
    }

    public static void sendUpdateSateNotificationRecord(ScreenRecordHelper.State state) {
        RxBus.getInstance().send(new BusEvent(RxBusType.STATE_PAUSE_OR_PLAY, state));
    }

    public static void sendLoadTargetAppFinished() {
        RxBus.getInstance().send(new BusEvent(RxBusType.LOAD_TARGET_APP_FINISHED, null));
    }

    public static void sendStartRecord() {
        RxBus.getInstance().send(new BusEvent(RxBusType.RECORD, null));
    }

}
