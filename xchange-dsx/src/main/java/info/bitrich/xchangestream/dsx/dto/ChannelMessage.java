package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

/**
 * @author rimalon
 */
public class ChannelMessage {
    private DsxEventType event;
    private String channel;


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
