package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rimalon
 */
public class DsxOrderbookMessage extends InstrumentMessage {
    private final List<BigDecimal[]> asks;
    private final List<BigDecimal[]> bids;
    private final long timestamp;

    public DsxOrderbookMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") String channel,
            @JsonProperty("mode") DsxModeType mode,
            @JsonProperty("instrument") String instrument,
            @JsonProperty("limit") List<BigDecimal[]> asks,
            @JsonProperty("bids") List<BigDecimal[]> bids,
            @JsonProperty("timestamp") long timestamp) {
        super(event, channel, instrument, mode);
        this.asks = asks;
        this.bids = bids;
        this.timestamp = timestamp;
    }

    public List<BigDecimal[]> getAsks() {
        return asks;
    }

    public List<BigDecimal[]> getBids() {
        return bids;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
