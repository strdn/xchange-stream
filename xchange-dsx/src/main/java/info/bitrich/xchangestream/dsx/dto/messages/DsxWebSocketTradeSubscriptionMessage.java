package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxWebSocketTradeSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Long prevDealId;

    @ConstructorProperties({"rid", "event", "channel", "instrument", "instrumentType", "prevDealId"})
    public DsxWebSocketTradeSubscriptionMessage(long rid, DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType, Long prevDealId) {
        super(rid, event, channel, instrument, instrumentType);
        this.prevDealId = prevDealId;
    }

    public Long getPrevDealId() {
        return prevDealId;
    }
}
