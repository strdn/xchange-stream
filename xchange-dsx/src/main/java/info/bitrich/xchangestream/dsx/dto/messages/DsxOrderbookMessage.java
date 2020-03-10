package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.DsxOrderBook;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author rimalon
 */
public class DsxOrderbookMessage extends InstrumentChannelMessage {
    private final List<BigDecimal[]> asks;
    private final List<BigDecimal[]> bids;
    private final long timestamp;

    @ConstructorProperties({"event", "channel", "instrument", "instrumentType", "asks", "bids", "timestamp"})
    public DsxOrderbookMessage(DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType,
                               List<BigDecimal[]> asks, List<BigDecimal[]> bids, long timestamp) {
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
        if (getEvent() == DsxEventType.UPDATE) {
            return orderbook.updateOrderBook(this);
        }
        return new DsxOrderBook(this);
    }

}
