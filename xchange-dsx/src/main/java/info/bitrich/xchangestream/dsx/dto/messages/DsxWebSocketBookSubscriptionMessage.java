package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketBookSubscriptionMessage extends DsxWebSocketInstrumentSubscriptionMessage {
    private final Integer limit;

    @ConstructorProperties({"rid", "event", "instrument", "instrumentType", "limit"})
    public DsxWebSocketBookSubscriptionMessage(long rid, DsxEventType event, String instrument, DsxInstrumentType instrumentType, Integer limit) {
        super(rid, event, instrument, instrumentType);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
