package com.mtg.screenrecorder.base.rx;



import io.reactivex.rxjava3.functions.Consumer;

public class CallBackRxBus implements Consumer<BusEvent> {
    private CallbackEventView callbackEventView;

    public CallBackRxBus(CallbackEventView callbackEventView) {
        this.callbackEventView = callbackEventView;
    }

    @Override
    public void accept(BusEvent busEvent) throws Throwable {
        callbackEventView.onReceivedEvent(busEvent.getType(), busEvent.getData());
    }
}
