package com.mtg.screenrecorder.base.rx;

public class BusEvent {
    private RxBusType type;
    private Object data;

    public BusEvent(RxBusType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public RxBusType getType() {
        return type;
    }

    public void setType(RxBusType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
