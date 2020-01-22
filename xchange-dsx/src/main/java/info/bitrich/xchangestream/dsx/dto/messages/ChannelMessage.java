package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

/**
 * @author rimalon
 */
public class ChannelMessage {
    private final DsxEventType event;
    private final String channel;

    public ChannelMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") String channel) {
        this.channel = channel;
        this.event = event;
    }

    public DsxEventType getEvent() {
        return event;
    }

    public String getChannel() {
        return channel;
    }
}
