package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

/**
 * @author rimalon
 */
public class InstrumentChannelMessage extends DsxChannelMessage {
    private final DsxInstrumentType instrumentType;
    private final String instrument;

    public InstrumentChannelMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") DsxChannel channel,
                                    @JsonProperty("instrument") String instrument, @JsonProperty("instrumentType") DsxInstrumentType instrumentType) {
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
