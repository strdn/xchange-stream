package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class DsxWebSocketTradeSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Integer prevDealsCount;

    public DsxWebSocketTradeSubscriptionMessage(@JsonProperty("rid") long rid,
                                                @JsonProperty("event") DsxEventType event,
                                                @JsonProperty("instrumentType") DsxInstrumentType instrumentType,
                                                @JsonProperty("instrument") String instrument,
                                                @JsonProperty("prevDealsCount") Integer prevDealsCount) {
        super(rid, event, instrumentType, instrument);
        this.prevDealsCount = prevDealsCount;
    }

    public Integer getPrevDealsCount() {
        return prevDealsCount;
    }
}
