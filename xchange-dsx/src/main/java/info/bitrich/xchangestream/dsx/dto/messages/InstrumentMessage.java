package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class InstrumentMessage extends DsxEventMessage {
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    public InstrumentMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("instrument") String instrument,
                             @JsonProperty("instrumentType") DsxInstrumentType instrumentType) {
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
