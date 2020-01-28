package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.DsxOrderBook;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rimalon
 */
public class DsxOrderbookMessage extends InstrumentChannelMessage {
    private final List<BigDecimal[]> asks;
    private final List<BigDecimal[]> bids;
    private final long timestamp;

    public DsxOrderbookMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") DsxChannel channel,
            @JsonProperty("instrumentType") DsxInstrumentType instrumentType,
            @JsonProperty("instrument") String instrument,
            @JsonProperty("limit") List<BigDecimal[]> asks,
            @JsonProperty("bids") List<BigDecimal[]> bids,
            @JsonProperty("timestamp") long timestamp) {
        super(event, channel, instrument, instrumentType);
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

    public DsxOrderBook toDsxOrderBook(DsxOrderBook orderbook) {
        if (getEvent() == DsxEventType.update) {
            return orderbook.updateOrderBook(this);
        }
        return new DsxOrderBook(this);
    }

}
