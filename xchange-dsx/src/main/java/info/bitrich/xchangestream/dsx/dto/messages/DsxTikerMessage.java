package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTicker;

/**
 * @author rimalon
 */
public class DsxTikerMessage extends InstrumentMessage {
    private final DSXTicker ticker;

    @JsonCreator
    public DsxTikerMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel, @JsonProperty String instrument,
                           @JsonProperty("mode") DsxModeType mode, @JsonProperty("ticker") DSXTicker ticker) {
        super(event, channel, instrument, mode);
        this.ticker = ticker;
    }

    public DSXTicker getTicker() {
        return ticker;
    }
}
