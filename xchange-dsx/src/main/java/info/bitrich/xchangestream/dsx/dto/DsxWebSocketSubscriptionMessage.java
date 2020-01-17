package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends ChannelMessage {

    private final int rid;
    private final String instrument;
    private final Integer limit;

    public DsxWebSocketSubscriptionMessage(@JsonProperty("rid") int rid,
                                           @JsonProperty("event") DsxEventType event,
                                           @JsonProperty("channel") String channel,
                                           @JsonProperty("instrument") String instrument,
                                           @JsonProperty("limit") Integer limit) {
        super(event, channel);
        this.rid = rid;
        this.instrument = instrument;
        this.limit = limit;
    }

    public int getRid() {
        return rid;
    }

    public String getInstrument() {
        return instrument;
    }

    public Integer getLimit() {
        return limit;
    }
}
