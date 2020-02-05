package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

import java.beans.ConstructorProperties;

public class DsxEventMessage {
    private final DsxEventType event;

    @ConstructorProperties("event")
    public DsxEventMessage(DsxEventType event) {
        this.event = event;
    }

    public DsxEventType getEvent() {
        return event;
    }
}
