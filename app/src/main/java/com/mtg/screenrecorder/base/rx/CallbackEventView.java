package com.mtg.screenrecorder.base.rx;

public interface CallbackEventView {
    default void onReceivedEvent(RxBusType type, Object data) {

    }
}
