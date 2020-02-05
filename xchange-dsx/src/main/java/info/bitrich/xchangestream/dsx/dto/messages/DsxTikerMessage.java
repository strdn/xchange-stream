package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTicker;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxTikerMessage extends InstrumentChannelMessage {
    private final DSXTicker ticker;

    @ConstructorProperties({"event", "channel", "instrument", "instrumentType", "ticker"})
    public DsxTikerMessage(DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType, DSXTicker ticker) {
        super(event, channel, instrument, instrumentType);
        this.ticker = ticker;
    }

    public DSXTicker getTicker() {
        return ticker;
    }
}
