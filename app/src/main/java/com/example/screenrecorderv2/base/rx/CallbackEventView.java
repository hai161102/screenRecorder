package com.example.screenrecorderv2.base.rx;

public interface CallbackEventView {
    default void onReceivedEvent(RxBusType type, Object data) {

    }
}
