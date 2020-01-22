package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

/**
 * @author rimalon
 */
public class ChannelMessage {
    private final DsxEventType event;
    private final DsxChannel channel;

    public ChannelMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") DsxChannel channel) {
        this.channel = channel;
        this.event = event;
    }

    public DsxEventType getEvent() {
        return event;
    }

    public DsxChannel getChannel() {
        return channel;
    }
}
