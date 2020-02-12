package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

public class DsxWebSocketInstrumentSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final DsxChannel channel;
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    @ConstructorProperties({"rid", "event", "channel", "instrument", "instrumentType"})
    public DsxWebSocketInstrumentSubscriptionMessage(long rid, DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType) {
        super(rid, event);
        this.channel = channel;
        this.instrument = instrument;
        this.instrumentType = instrumentType;
    }

    public DsxInstrumentType getInstrumentType() {
        return instrumentType;
    }

    public String getInstrument() {
        return instrument;
    }

    public DsxChannel getChannel() {
        return channel;
    }
}
