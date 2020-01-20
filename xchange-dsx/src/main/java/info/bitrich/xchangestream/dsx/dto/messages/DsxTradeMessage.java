package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;

/**
 * @author rimalon
 */
public class DsxTradeMessage extends ChannelMessage {
    private final String instrument;
    private final DSXTrade[] trades;

    public DsxTradeMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel,
                           @JsonProperty("mode") DsxModeType mode, @JsonProperty("instrument") String instrument, @JsonProperty("trade") DSXTrade[] trade) {
        super(event, channel, mode);
        this.instrument = instrument;
        this.trades = trade;
    }

    public DSXTrade[] getTrades() { return trades; }
}
