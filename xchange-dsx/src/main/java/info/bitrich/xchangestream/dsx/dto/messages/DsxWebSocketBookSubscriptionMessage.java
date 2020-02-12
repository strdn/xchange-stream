package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketBookSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Integer limit;

    @ConstructorProperties({"rid", "event", "channel", "instrument", "instrumentType", "limit"})
    public DsxWebSocketBookSubscriptionMessage(long rid, DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType, Integer limit) {
        super(rid, event, channel, instrument, instrumentType);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
