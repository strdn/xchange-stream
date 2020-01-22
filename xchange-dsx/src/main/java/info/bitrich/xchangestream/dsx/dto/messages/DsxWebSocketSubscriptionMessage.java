package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends InstrumentMessage {
    private final int rid;
    private final Integer limit;

    public DsxWebSocketSubscriptionMessage(@JsonProperty("rid") int rid,
                                           @JsonProperty("event") DsxEventType event,
                                           @JsonProperty("channel") String channel,
                                           @JsonProperty("mode") DsxModeType mode,
                                           @JsonProperty("instrument") String instrument,
                                           @JsonProperty("limit") Integer limit) {
        super(event, channel, instrument, mode);
        this.rid = rid;
        this.limit = limit;
    }

    public int getRid() {
        return rid;
    }

    public Integer getLimit() {
        return limit;
    }
}
