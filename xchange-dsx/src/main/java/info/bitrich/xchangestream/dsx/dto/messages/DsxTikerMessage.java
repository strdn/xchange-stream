package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTicker;

/**
 * @author rimalon
 */
public class DsxTikerMessage extends ChannelMessage {
    private DSXTicker ticker;

    @JsonCreator
    public DsxTikerMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel,
                           @JsonProperty("mode") DsxModeType mode, @JsonProperty("ticker") DSXTicker ticker) {
        super(event, channel, mode);
        this.ticker = ticker;
    }

    public DSXTicker getTicker() {
        return ticker;
    }
}
