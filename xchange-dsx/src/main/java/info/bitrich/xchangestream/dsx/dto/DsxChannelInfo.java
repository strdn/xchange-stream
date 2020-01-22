package info.bitrich.xchangestream.dsx.dto;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;

public class DsxChannelInfo {

    private DsxChannel channel;
    private String instrument;
    private DsxInstrumentType instrumentType;

    public DsxChannelInfo(DsxChannel channel, String instrument, DsxInstrumentType instrumentType) {
        this.channel = channel;
        this.instrument = instrument;
        this.instrumentType = instrumentType;
    }

    public DsxChannel getChannel() {
        return channel;
    }

    public String getInstrument() {
        return instrument;
    }

    public DsxInstrumentType getInstrumentType() {
        return instrumentType;
    }

    @Override
    public String toString() {
        return channel + "/" + instrument + "/" + instrumentType;
    }
}
