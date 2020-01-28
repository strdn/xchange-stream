package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class DsxWebSocketBookSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Integer limit;
    public DsxWebSocketBookSubscriptionMessage(@JsonProperty("rid") long rid,
                                               @JsonProperty("event") DsxEventType event,
                                               @JsonProperty("instrumentType") DsxInstrumentType instrumentType,
                                               @JsonProperty("instrument") String instrument,
                                               @JsonProperty("limit") Integer limit) {
        super(rid, event, instrumentType, instrument);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
