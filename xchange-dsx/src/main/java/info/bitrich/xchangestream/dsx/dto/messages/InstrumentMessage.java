package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class InstrumentMessage extends DsxEventMessage {
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    @ConstructorProperties({"event", "instrument", "instrumentType"})
    public InstrumentMessage(DsxEventType event, String instrument, DsxInstrumentType instrumentType) {
        super(event);
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
