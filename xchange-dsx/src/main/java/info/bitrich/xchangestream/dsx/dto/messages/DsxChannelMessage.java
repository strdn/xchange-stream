package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

/**
 * @author rimalon
 */
public class DsxChannelMessage extends DsxEventMessage{
    private final DsxChannel channel;

    public DsxChannelMessage(
            @JsonProperty("event") DsxEventType event,
            @JsonProperty("channel") DsxChannel channel) {
        super(event);
        this.channel = channel;
    }

    public DsxChannel getChannel() {
        return channel;
    }
}
