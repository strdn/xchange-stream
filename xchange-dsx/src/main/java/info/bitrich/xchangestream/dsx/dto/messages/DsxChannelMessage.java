package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

import java.beans.ConstructorProperties;

/**
 * @author rimalon
 */
public class DsxChannelMessage extends DsxEventMessage{
    private final DsxChannel channel;

    @ConstructorProperties({"event", "channel"})
    public DsxChannelMessage(DsxEventType event, DsxChannel channel) {
        super(event);
        this.channel = channel;
    }

    public DsxChannel getChannel() {
        return channel;
    }
}
