package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends InstrumentChannelMessage {
    private final long rid;

    @ConstructorProperties({"rid", "event", "channel", "instrument", "instrumentType"})
    public DsxWebSocketSubscriptionMessage(long rid, DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType) {
        super(event, channel, instrument, instrumentType);
        this.rid = rid;
    }

    public long getRid() {
        return rid;
    }
}
