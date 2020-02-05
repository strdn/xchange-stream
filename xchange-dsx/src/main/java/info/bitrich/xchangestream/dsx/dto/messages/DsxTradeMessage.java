package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxTradeMessage extends InstrumentChannelMessage {
    private final DSXTrade[] trades;

    @ConstructorProperties({"event", "channel", "instrument", "instrumentType", "trade"})
    public DsxTradeMessage(DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType, DSXTrade[] trades) {
        super(event, channel, instrument, instrumentType);
        this.trades = trades;
    }

    public DSXTrade[] getTrades() { return trades; }
}
