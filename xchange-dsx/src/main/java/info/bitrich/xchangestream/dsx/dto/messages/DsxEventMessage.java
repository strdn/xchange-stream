package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

public class DsxEventMessage {
    private final DsxEventType event;

    public DsxEventMessage(DsxEventType event) {
        this.event = event;
    }

    public DsxEventType getEvent() {
        return event;
    }
}
