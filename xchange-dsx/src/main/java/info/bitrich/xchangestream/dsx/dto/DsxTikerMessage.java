package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTicker;

/**
 * @author rimalon
 */
public class DsxTikerMessage extends ChannelMessage {
    private DSXTicker ticker;

    @JsonCreator
    public DsxTikerMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel, @JsonProperty("ticker") DSXTicker ticker) {
        super(event, channel);
        this.ticker = ticker;
    }

    public DSXTicker getTicker() {
        return ticker;
    }
}
