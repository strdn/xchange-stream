package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannelsType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;

/**
 * @author rimalon
 */
public class DsxTradeMessage extends InstrumentMessage {
    private final DSXTrade[] trades;

    public DsxTradeMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") DsxChannelsType channel,
                           @JsonProperty("instrument") String instrument, @JsonProperty("mode") DsxModeType mode, @JsonProperty("trade") DSXTrade[] trades) {
        super(event, channel, instrument, mode);
        this.trades = trades;
    }

    public DSXTrade[] getTrades() { return trades; }
}
