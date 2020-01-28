package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTicker;

/**
 * @author rimalon
 */
public class DsxTikerMessage extends InstrumentChannelMessage {
    private final DSXTicker ticker;

    @JsonCreator
    public DsxTikerMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") DsxChannel channel, @JsonProperty String instrument,
                           @JsonProperty("instrumentType") DsxInstrumentType instrumentType, @JsonProperty("ticker") DSXTicker ticker) {
        super(event, channel, instrument, instrumentType);
        this.ticker = ticker;
    }

    public DSXTicker getTicker() {
        return ticker;
    }
}
