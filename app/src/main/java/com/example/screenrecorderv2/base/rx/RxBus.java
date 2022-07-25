package com.example.screenrecorderv2.base.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxBus {
    private static RxBus instance;
    private PublishSubject<BusEvent> bus;

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }


    public RxBus() {
        bus = PublishSubject.create();
    }

    public void send(BusEvent data) {
        bus.onNext(data);
    }

    public Observable<BusEvent> toObservable() {
        return bus;
    }

    public Disposable subscribe(CallBackRxBus callBackRxBus){
        return RxBus.getInstance().toObservable().subscribe(callBackRxBus);
    }
}
