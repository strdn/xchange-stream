package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;

/**
 * @author rimalon
 */
public class DsxTradeMessage extends InstrumentMessage {
    private final DSXTrade[] trades;

    public DsxTradeMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") DsxChannel channel,
                           @JsonProperty("instrument") String instrument, @JsonProperty("instrumentType") DsxInstrumentType instrumentType, @JsonProperty("trade") DSXTrade[] trades) {
        super(event, channel, instrument, instrumentType);
        this.trades = trades;
    }

    public DSXTrade[] getTrades() { return trades; }
}
