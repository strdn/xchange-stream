package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannelsType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class DsxWebSocketOrderbookSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final Integer limit;
    public DsxWebSocketOrderbookSubscriptionMessage(@JsonProperty("rid") long rid,
                                                    @JsonProperty("event") DsxEventType event,
                                                    @JsonProperty("channel") DsxChannelsType channel,
                                                    @JsonProperty("mode") DsxModeType mode,
                                                    @JsonProperty("instrument") String instrument,
                                                    @JsonProperty("limit") Integer limit) {
        super(rid, event, channel, mode, instrument);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
