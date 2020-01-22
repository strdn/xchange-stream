package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class InstrumentMessage extends ChannelMessage {
    private final DsxModeType mode;
    private final String instrument;

    public InstrumentMessage(@JsonProperty("event") DsxEventType event, @JsonProperty("channel") String channel,
                             @JsonProperty("instrument") String instrument, @JsonProperty("mode") DsxModeType mode) {
        super(event, channel);
        this.instrument = instrument;
        this.mode = mode;
    }

    public DsxModeType getMode() {
        return mode;
    }

    public String getInstrument() {
        return instrument;
    }
}
