package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends InstrumentMessage {
    private final long rid;

    @ConstructorProperties({"rid", "event", "instrument", "instrumentType"})
    public DsxWebSocketSubscriptionMessage(long rid, DsxEventType event, String instrument, DsxInstrumentType instrumentType) {
        super(event, instrument, instrumentType);
        this.rid = rid;
    }

    public long getRid() {
        return rid;
    }
}
