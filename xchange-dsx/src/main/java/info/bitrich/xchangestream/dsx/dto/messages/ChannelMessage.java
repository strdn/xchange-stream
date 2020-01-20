package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class ChannelMessage {
    private DsxEventType event;
    private String channel;
    private DsxModeType mode;


    public ChannelMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") String channel,
            @JsonProperty("mode") DsxModeType mode) {
        this.channel = channel;
        this.event = event;
        this.mode = mode;
    }

    public DsxEventType getEvent() {
        return event;
    }

    public String getChannel() {
        return channel;
    }

    public DsxModeType getMode() { return mode; }
}
