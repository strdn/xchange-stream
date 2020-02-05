package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class InstrumentChannelMessage extends DsxChannelMessage {
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    @ConstructorProperties({"event", "channel", "instrument", "instrumentType"})
    public InstrumentChannelMessage(DsxEventType event, DsxChannel channel, String instrument, DsxInstrumentType instrumentType) {
        super(event, channel);
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
