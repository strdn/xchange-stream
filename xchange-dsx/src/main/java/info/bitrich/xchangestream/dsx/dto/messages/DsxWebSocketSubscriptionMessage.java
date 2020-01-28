package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class DsxWebSocketSubscriptionMessage extends InstrumentMessage {
    private final long rid;

    public DsxWebSocketSubscriptionMessage(@JsonProperty("rid") long rid,
                                           @JsonProperty("event") DsxEventType event,
                                           @JsonProperty("mode") DsxInstrumentType mode,
                                           @JsonProperty("instrument") String instrument) {
        super(event, instrument, mode);
        this.rid = rid;
    }

    public long getRid() {
        return rid;
    }
}
