package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

public class DsxWebSocketInstrumentSubscriptionMessage extends DsxWebSocketSubscriptionMessage {
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    @ConstructorProperties({"rid", "event", "instrument", "instrumentType"})
    public DsxWebSocketInstrumentSubscriptionMessage(long rid, DsxEventType event, String instrument, DsxInstrumentType instrumentType) {
        super(rid, event);
        this.instrument = instrument;
        this.instrumentType = instrumentType;
    }

    public DsxInstrumentType getInstrumentType() {
        return instrumentType;
    }

    public String getInstrument() {
        return instrument;
    }
}
