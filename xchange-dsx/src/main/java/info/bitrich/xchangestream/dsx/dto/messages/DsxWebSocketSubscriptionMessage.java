package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends DsxEventMessage {
    private final long rid;

    @ConstructorProperties({"rid", "event"})
    public DsxWebSocketSubscriptionMessage(long rid, DsxEventType event) {
        super(event);
        this.rid = rid;
    }

    public long getRid() {
        return rid;
    }
}
