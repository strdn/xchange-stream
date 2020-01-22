package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class DsxWebSocketOrderbookSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Integer limit;
    public DsxWebSocketOrderbookSubscriptionMessage(@JsonProperty("rid") long rid,
                                                    @JsonProperty("event") DsxEventType event,
                                                    @JsonProperty("channel") DsxChannel channel,
                                                    @JsonProperty("instrumentType") DsxInstrumentType instrumentType,
                                                    @JsonProperty("instrument") String instrument,
                                                    @JsonProperty("limit") Integer limit) {
        super(rid, event, channel, instrumentType, instrument);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
