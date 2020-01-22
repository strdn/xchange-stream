package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannelsType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends InstrumentMessage {
    private final long rid;

    public DsxWebSocketSubscriptionMessage(@JsonProperty("rid") long rid,
                                           @JsonProperty("event") DsxEventType event,
                                           @JsonProperty("channel") DsxChannelsType channel,
                                           @JsonProperty("mode") DsxModeType mode,
                                           @JsonProperty("instrument") String instrument) {
        super(event, channel, instrument, mode);
        this.rid = rid;
    }

    public long getRid() {
        return rid;
    }
}
