package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class DsxWebSocketTradeSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Long prevDealId;

    public DsxWebSocketTradeSubscriptionMessage(@JsonProperty("rid") long rid,
                                                @JsonProperty("event") DsxEventType event,
                                                @JsonProperty("instrumentType") DsxInstrumentType instrumentType,
                                                @JsonProperty("instrument") String instrument,
                                                @JsonProperty("prevDealId") Long prevDealId) {
        super(rid, event, instrumentType, instrument);
        this.prevDealId = prevDealId;
    }

    public Long getPrevDealId() {
        return prevDealId;
    }
}
