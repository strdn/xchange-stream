package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;

/**
 * @author rimalon
 */
public class DsxTradeMessage extends ChannelMessage {
    private DSXTrade trade;

    public DsxTradeMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel, @JsonProperty("trade") DSXTrade trade) {
        super(event, channel);
        this.trade = trade;
    }

    public DSXTrade getTrade() { return trade; }
}
